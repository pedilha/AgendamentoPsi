# Notas técnicas — Sistema de Agendamento

Decisões e porquês, registrados fase a fase. Vira insumo do README final (Fase 11).

## Fase 0 — Setup
- Spring Boot + PostgreSQL rodando via Docker Compose, app fora do container (mvn direto).
- Porta do Postgres mapeada em 5433 (não 5432) porque já existia um Postgres nativo
  instalado como serviço do Windows, competindo pela porta padrão.
- `application.yml` em vez de `.properties` — mais legível pra config aninhada.

## Fase 1 — Modelagem
- 6 entidades: Usuario, Psicologo, Paciente, Disponibilidade, BloqueioAgenda, Agendamento.
- Psicologo e Paciente usam `@OneToOne` + `@MapsId` com Usuario — id compartilhado,
  não duplicado. Decisão consciente: **não** usar herança JPA (Usuario → Psicologo/Paciente),
  porque não há polimorfismo real no caso de uso — sempre sabemos o papel de antemão,
  então composição é mais simples e evita coluna discriminadora redundante com o campo `role`.
- Enums sempre com `@Enumerated(EnumType.STRING)` — nunca ORDINAL (frágil a reordenação).
- Sem setters nas entidades — só construtor + getters. Mudanças de estado exigem métodos
  de domínio explícitos (a criar quando a necessidade real aparecer).
- Migrations Flyway (`V1__create_tables.sql`) controlam o schema — não `ddl-auto: update`.
- Convenção: SQL sempre snake_case, Java sempre camelCase (evita mismatch silencioso
  entre nome de coluna gerado pelo Hibernate e o real no banco).
- Status inicial de Agendamento: sempre `CONFIRMADO` direto (decisão de produto — sem
  fluxo de aprovação da psicóloga no v1).
- Campo `telefone` no Paciente (não CPF) — CPF avaliado e descartado por enquanto:
  nenhuma feature do roadmap usa, e é dado sensível (LGPD) sem justificativa de uso ainda.
  Reconsiderar se/quando existir feature de emissão de recibo pra IR.

## Fase 2 — Autenticação
- Spring Security com sessão (cookie `JSESSIONID`), não JWT — decisão correta dado
  Thymeleaf (server-side rendering); JWT é solução pra frontend desacoplado.
- Senha com BCrypt (`PasswordEncoder` bean).
- Autorização por role via `@ManyToOne`... não, via `hasRole()` nos `requestMatchers`,
  ordem importa (mais específico primeiro, `anyRequest()` sempre por último).
- `DataSeeder` (`CommandLineRunner`) cria usuário de teste em dev — **remover ou isolar
  atrás de profile antes do deploy (Fase 10)**.
- Pegadinha registrada: `DataSeeder` inicial criava só o `Usuario`, esquecendo o
  `Psicologo` associado — quebrou silenciosamente até a Fase 3 tentar consultar a tabela.
  Lição: sempre que um Usuario nasce com role PSICOLOGO/PACIENTE, a entidade associada
  precisa nascer junto, na mesma operação.

## Fase 3 (completa) — Disponibilidade e BloqueioAgenda
- CRUD completo nas duas entidades: criar, listar, editar, excluir.
- Padrão de edição sem setters: cada entidade ganhou um segundo construtor
  aceitando `id` como parâmetro, usado só para reconstrução ao atualizar.
  `Service.atualizar()` sempre monta um objeto novo com esse construtor e
  chama `save()` — nunca muta o objeto existente. Mantém a regra "sem setters"
  da Fase 1 mesmo em operações de update.
- Verificação de posse obrigatória em toda operação de update/delete: compara
  `entidade.getPsicologo().getId()` com o `id` do psicólogo autenticado antes
  de agir. Sem isso, qualquer usuário logado poderia editar/excluir dados de
  outra psicóloga só adivinhando o id na URL. Esse padrão vai se repetir em
  `Agendamento` na Fase 5 — não esquecer lá.
- `@PathVariable` para capturar id na URL (`/recurso/{id}/acao`), diferente de
  `@RequestParam` (campos de formulário). Erro comum: confundir os dois.
- Botão de excluir sempre via `<form method="post">`, nunca `<a href>` — a
  rota é POST, link sempre faria GET e devolveria 405.

## Troubleshooting recorrente (pra não repetir)
- Depois de reiniciar o PC, sempre `docker ps` antes de `mvn spring-boot:run` — 
  container não sobrevive a reboot.
- Se o Maven disser "Nothing to compile" e o comportamento não mudou: o arquivo
  provavelmente não foi salvo de fato no editor antes de rodar. Confirmar salvamento
  (Ctrl+S) antes de rodar, ou usar `mvn clean spring-boot:run` pra forçar.
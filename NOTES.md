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

## Fase 3 — Disponibilidade (completa)
- Controller usa `Authentication.getName()` pra pegar o email da sessão ativa,
  e a partir dele busca o Psicologo (`findByUsuarioEmail`) — é assim que toda tela
  autenticada vai descobrir "quem está logado".
- Camada Service criada (`DisponibilidadeService`) pra hospedar regra de negócio —
  Controller não deve conter lógica de validação, só orquestrar requisição/resposta.
- Validação de sobreposição de horário: fórmula `inicioA < fimB E inicioB < fimA`,
  clássica pra checar colisão de dois intervalos de tempo. Mesma lógica que vai
  aparecer de novo, mais complexa, no motor de slots (Fase 4).
- `@RequestParam` em campos individuais no controller, não a entidade inteira como
  parâmetro — decisão consistente com "sem setters nas entidades" (Fase 1): permitir
  bind direto do formulário pra entidade exigiria setters, reabrindo a porta que
  fechamos de propósito.
- Pendência de polish (não bloqueia): erro de validação hoje estoura 500 genérico
  em vez de voltar pro formulário com mensagem amigável. Resolver junto com padrão
  parecido que vai aparecer na Fase 5.

## Troubleshooting recorrente (pra não repetir)
- Depois de reiniciar o PC, sempre `docker ps` antes de `mvn spring-boot:run` — 
  container não sobrevive a reboot.
- Se o Maven disser "Nothing to compile" e o comportamento não mudou: o arquivo
  provavelmente não foi salvo de fato no editor antes de rodar. Confirmar salvamento
  (Ctrl+S) antes de rodar, ou usar `mvn clean spring-boot:run` pra forçar.
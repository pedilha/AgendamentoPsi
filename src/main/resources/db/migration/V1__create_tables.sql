CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE paciente (
    id BIGINT PRIMARY KEY REFERENCES usuario(id),
    telefone VARCHAR(20) NOT NULL
);

CREATE TABLE psicologo (
    id BIGINT PRIMARY KEY REFERENCES usuario(id),
    crp VARCHAR(20) NOT NULL UNIQUE,
    duracao_consulta_minutos INT NOT NULL
);

CREATE TABLE disponibilidade (
    id BIGSERIAL PRIMARY KEY,
    psicologo_id BIGINT NOT NULL REFERENCES psicologo(id),
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL
);

CREATE TABLE bloqueio_agenda (
    id BIGSERIAL PRIMARY KEY,
    psicologo_id BIGINT NOT NULL REFERENCES psicologo(id),
    data DATE NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL
);

CREATE TABLE agendamento (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES paciente(id),
    psicologo_id BIGINT NOT NULL REFERENCES psicologo(id),
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO'
);
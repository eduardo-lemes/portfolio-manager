# Portfolio Manager

Sistema de gerenciamento de portfólio de projetos desenvolvido com Spring Boot.

## Stack

- Java 17
- Spring Boot 3.2.5
- PostgreSQL 16
- JPA + Hibernate
- Spring Security (Basic Auth)
- SpringDoc OpenAPI (Swagger UI)
- MapStruct
- JUnit 5 + Mockito

## Pré-requisitos

- Java 17+
- Maven 3.9+
- Docker + Docker Compose

## Como rodar

**1. Subir o banco de dados**
```bash
docker compose up -d
```

**2. Rodar a aplicação**
```bash
mvn spring-boot:run
```

**3. Acessar o Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

## Autenticação

Todos os endpoints utilizam HTTP Basic Auth.

| Usuário | Senha    |
|---------|----------|
| admin   | admin123 |

## Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/membros` | Cria membro |
| GET | `/api/membros` | Lista membros |
| POST | `/api/projetos` | Cria projeto |
| GET | `/api/projetos` | Lista projetos (paginado + filtros) |
| PUT | `/api/projetos/{id}` | Atualiza projeto |
| DELETE | `/api/projetos/{id}` | Remove projeto |
| PATCH | `/api/projetos/{id}/status` | Atualiza status |
| POST | `/api/projetos/{id}/membros/{membroId}` | Associa membro |
| GET | `/api/relatorio/portfolio` | Relatório do portfólio |

## Testes

```bash
mvn test
```

Relatório de cobertura JaCoCo gerado automaticamente em `target/site/jacoco/index.html`.

## Regras de negócio

- Classificação de risco calculada dinamicamente (Baixo / Médio / Alto)
- Transições de status seguem sequência obrigatória
- Apenas membros com atribuição `FUNCIONARIO` podem ser associados a projetos
- Limite de 1 a 10 membros por projeto
- Membro não pode estar em mais de 3 projetos ativos simultaneamente
- Projetos com status `INICIADO`, `EM_ANDAMENTO` ou `ENCERRADO` não podem ser excluídos

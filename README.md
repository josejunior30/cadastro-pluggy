# Cadastro - Pluggy

Aplicação full stack para cadastro de usuários, autenticação com JWT e integração com a Pluggy para conexão bancária, sincronização de contas e consulta de transações financeiras.

O projeto foi desenvolvido com foco em uma arquitetura backend organizada em camadas, frontend moderno com Angular, autenticação segura, integração com API externa, documentação OpenAPI, observabilidade e métricas.

---

## Visão geral

Este projeto demonstra competências práticas em desenvolvimento full stack, incluindo:

- Construção de API REST com Java e Spring Boot.
- Autenticação baseada em JWT.
- Proteção de endpoints com Spring Security.
- Organização backend em camadas: controller, service, repository, DTO, entities, security, config e exceptions.
- Integração com API externa da Pluggy.
- Sincronização de contas e transações financeiras.
- Persistência com Spring Data JPA.
- Resiliência em chamadas externas com Resilience4j, retry e circuit breaker.
- Tratamento global de exceções com respostas padronizadas.
- Documentação da API com Swagger/OpenAPI.
- Observabilidade com Spring Boot Actuator.
- Exposição de métricas com Micrometer e Prometheus.
- Frontend Angular com rotas protegidas, services, interceptors e guards.
- Consumo do backend via Angular HttpClient.
- Conteinerização com Docker.

---

## Funcionalidades

### Backend

- Login de usuários com geração de token JWT.
- CRUD de usuários.
- Proteção de endpoints autenticados.
- Criação de connect token da Pluggy.
- Sincronização manual de item Pluggy.
- Recebimento de webhooks da Pluggy.
- Listagem de contas bancárias vinculadas ao usuário autenticado.
- Listagem paginada de transações por conta.
- Tratamento global de exceções.
- Respostas padronizadas para erros da API.
- Validação de DTOs com Bean Validation.
- Logs para acompanhamento de autenticação, sincronização e webhooks.
- Cache temporário da API key da Pluggy.
- Retry e circuit breaker para chamadas externas.
- Testes automatizados com JUnit 5 e Mockito.
- Documentação interativa com Swagger/OpenAPI.
- Observabilidade com Spring Boot Actuator.
- Health checks da aplicação.
- Exposição de métricas com Micrometer.
- Endpoint Prometheus para coleta de métricas.

### Frontend

- Tela de login.
- Armazenamento do token JWT no navegador.
- Validação de autenticação pelo payload do token.
- Rotas protegidas com guard.
- Interceptor para envio automático do token JWT.
- Integração com Pluggy Connect.
- Navegação para contas e transações.
- Services dedicados para autenticação e Pluggy.
- Estrutura preparada para evolução de telas e componentes.
- Estilização com Tailwind CSS.

---

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- OAuth2 Client
- Bean Validation
- Maven
- H2 Database
- Resilience4j
- REST Client
- JWT
- Spring Boot Actuator
- Micrometer
- Prometheus Registry
- Springdoc OpenAPI
- JUnit 5
- Mockito
- Docker

### Frontend

- Angular 21
- TypeScript
- Angular Router
- Angular HttpClient
- RxJS
- Tailwind CSS
- Pluggy Connect SDK
- Vitest
- Prettier

---

## Arquitetura do projeto

```txt
cadastro-pluggy/
├── backend/
│   └── cadastro/
│       ├── src/main/java/com/junior/cadastro/
│       │   ├── DTO/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── entities/
│       │   ├── exceptions/
│       │   ├── repository/
│       │   ├── security/
│       │   ├── service/
│       │   └── util/
│       ├── src/main/resources/
│       │   ├── application.properties
│       │   └── application-test.properties
│       ├── src/test/java/com/junior/cadastro/
│       │   ├── controller/
│       │   ├── security/
│       │   └── service/
│       ├── Dockerfile
│       └── pom.xml
│
├── front-end/
│   └── cadastro/
│       ├── src/app/
│       │   ├── guard/
│       │   ├── interceptors/
│       │   ├── models/
│       │   ├── pages/
│       │   ├── services/
│       │   ├── app.routes.ts
│       │   └── app.config.ts
│       ├── Dockerfile
│       └── package.json
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Documentação da API

A API possui documentação interativa com Swagger/OpenAPI.

Com a aplicação em execução, acesse:

```txt
http://localhost:8080/swagger-ui.html
```

Ou:

```txt
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI também fica disponível em:

```txt
http://localhost:8080/v3/api-docs
```

### Como testar endpoints protegidos no Swagger

Para testar endpoints autenticados:

1. Execute `POST /auth/login`.
2. Copie o valor retornado no campo `token`.
3. Clique em **Authorize** no Swagger.
4. Cole apenas o JWT, sem o prefixo `Bearer`.
5. Clique em **Authorize**.
6. Execute os endpoints protegidos normalmente.

---

## Principais endpoints da API

### Autenticação

| Método | Endpoint | Autenticação | Descrição |
|---|---|---:|---|
| `POST` | `/auth/login` | Não | Realiza login e retorna um token JWT. |

Exemplo de resposta:

```json
{
  "token": "jwt-token"
}
```

---

### Usuários

| Método | Endpoint | Autenticação | Descrição |
|---|---|---:|---|
| `GET` | `/user` | Sim | Lista todos os usuários. |
| `GET` | `/user/{id}` | Sim | Busca um usuário pelo ID. |
| `POST` | `/user` | Não | Cria um novo usuário. |
| `PUT` | `/user/{id}` | Sim | Atualiza os dados de um usuário. |
| `DELETE` | `/user/{id}` | Sim | Remove um usuário. |

Possíveis respostas de erro:

| Status | Motivo |
|---:|---|
| `400` | JSON inválido, parâmetro inválido ou erro de regra. |
| `401` | Token ausente, inválido ou expirado. |
| `403` | Usuário sem permissão. |
| `404` | Usuário ou role não encontrada. |
| `409` | E-mail já cadastrado. |
| `422` | Erro de validação nos campos enviados. |
| `500` | Erro interno inesperado. |

---

### Pluggy

| Método | Endpoint | Autenticação | Descrição |
|---|---|---:|---|
| `POST` | `/pluggy/connect-token` | Sim | Gera o connect token para abrir o Pluggy Connect. |
| `POST` | `/pluggy/items/sync` | Sim | Sincroniza contas e transações de um item Pluggy. |
| `GET` | `/pluggy/accounts` | Sim | Lista as contas bancárias do usuário autenticado. |
| `GET` | `/pluggy/accounts/{accountId}/transactions` | Sim | Lista transações paginadas de uma conta. |

Possíveis respostas de erro:

| Status | Motivo |
|---:|---|
| `400` | JSON inválido ou parâmetro inválido. |
| `401` | Token ausente, inválido ou expirado. |
| `403` | Usuário sem permissão. |
| `422` | Erro de validação nos campos enviados. |
| `502` | Falha de integração com a Pluggy. |
| `500` | Erro interno inesperado. |

---

### Webhooks Pluggy

| Método | Endpoint | Autenticação | Descrição |
|---|---|---:|---|
| `POST` | `/webhooks/pluggy` | Secret | Recebe eventos enviados pela Pluggy. |

O webhook pode validar o segredo recebido por header ou query parameter, dependendo da configuração usada no ambiente.

Exemplo de URL com secret:

```txt
/webhooks/pluggy?secret=pluggy-dev-secret
```

---

### Observabilidade e métricas

Os endpoints de observabilidade são expostos pelo Spring Boot Actuator e documentados no Swagger quando `springdoc.show-actuator=true` está habilitado.

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/actuator/health` | Mostra o status de saúde da aplicação. |
| `GET` | `/actuator/info` | Mostra informações gerais da aplicação. |
| `GET` | `/actuator/metrics` | Lista as métricas disponíveis. |
| `GET` | `/actuator/metrics/{requiredMetricName}` | Detalha uma métrica específica. |
| `GET` | `/actuator/prometheus` | Expõe métricas em formato Prometheus. |

Exemplos de métricas disponíveis:

```txt
http.server.requests
jvm.memory.used
jvm.threads.live
process.cpu.usage
system.cpu.usage
application.started.time
application.ready.time
resilience4j.circuitbreaker.calls
resilience4j.retry.calls
```

Observação: endpoints como `/actuator/metrics` e `/actuator/prometheus` podem expor detalhes internos da aplicação. Em ambiente real, mantenha esses endpoints protegidos.

---

## Resposta padrão de erro

A API utiliza uma resposta padronizada para erros:

```json
{
  "timestamp": "2026-05-02T12:00:00Z",
  "status": 422,
  "error": "Erro de validação",
  "message": "email: email obrigatório",
  "path": "/auth/login"
}
```

Principais códigos tratados:

| Status | Descrição |
|---:|---|
| `400` | Requisição inválida, JSON malformado ou parâmetro inválido. |
| `401` | Usuário não autenticado ou credenciais inválidas. |
| `403` | Usuário autenticado sem permissão. |
| `404` | Recurso não encontrado. |
| `409` | Conflito de dados, como e-mail já cadastrado. |
| `422` | Erro de validação nos campos enviados. |
| `500` | Erro interno inesperado. |
| `502` | Erro de integração com a Pluggy. |

---

## Como executar o projeto

### Pré-requisitos

Antes de começar, instale:

- Java 21
- Maven
- Node.js
- npm
- Angular CLI
- Git
- Docker
- Docker Compose

---

## Variáveis de ambiente

O backend utiliza credenciais da Pluggy e configurações de autenticação. Essas informações devem ficar fora do código-fonte.

Crie um arquivo `.env` localmente, sem versionar no Git:

```env
PLUGGY_BASE_URL=https://api.pluggy.ai
PLUGGY_CLIENT_ID=seu_client_id
PLUGGY_CLIENT_SECRET=seu_client_secret
PLUGGY_OAUTH_REDIRECT_URL=http://localhost:4200/pluggy/callback
PLUGGY_INCLUDE_SANDBOX=true
PLUGGY_WEBHOOK_URL=
PLUGGY_WEBHOOK_SECRET=
```

No `application.properties`, as variáveis são lidas assim:

```properties
pluggy.base-url=${PLUGGY_BASE_URL:https://api.pluggy.ai}
pluggy.client-id=${PLUGGY_CLIENT_ID}
pluggy.client-secret=${PLUGGY_CLIENT_SECRET}
pluggy.oauth-redirect-url=${PLUGGY_OAUTH_REDIRECT_URL:http://localhost:4200/pluggy/callback}
pluggy.include-sandbox=${PLUGGY_INCLUDE_SANDBOX:true}
pluggy.webhook-url=${PLUGGY_WEBHOOK_URL:}
pluggy.webhook-secret=${PLUGGY_WEBHOOK_SECRET:}
```

Configurações de Swagger, Actuator e métricas:

```properties
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.show-actuator=true

management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always
management.metrics.tags.application=cadastro-pluggy
```

Nunca envie o arquivo `.env` para o GitHub. Mantenha apenas um `.env.example` sem valores sensíveis.

---

## Executando o backend localmente

Acesse a pasta do backend:

```bash
cd backend/cadastro
```

Execute a aplicação com Maven Wrapper:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Por padrão, o backend ficará disponível em:

```txt
http://localhost:8080
```

---

## Executando o frontend localmente

Acesse a pasta do frontend:

```bash
cd front-end/cadastro
```

Instale as dependências:

```bash
npm install
```

Execute o servidor de desenvolvimento:

```bash
npm start
```

A aplicação ficará disponível em:

```txt
http://localhost:4200
```

---

## Executando com Docker

Na raiz do projeto, execute:

```bash
docker compose up --build
```

Caso o arquivo `.env` esteja em outro diretório, informe manualmente:

```bash
docker compose --env-file ./backend/cadastro/.env up --build
```

Para rodar em segundo plano:

```bash
docker compose --env-file ./backend/cadastro/.env up --build -d
```

Para parar os containers:

```bash
docker compose down
```

Para rebuildar sem cache:

```bash
docker compose build --no-cache
```

Aplicação disponível em:

```txt
Frontend: http://localhost:4200
Backend:  http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui.html
Health:   http://localhost:8080/actuator/health
```

---

## Testes automatizados

O backend possui testes automatizados com JUnit 5 e Mockito.

Para executar os testes:

```bash
cd backend/cadastro
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

Os testes cobrem partes importantes da aplicação, incluindo:

- Autenticação.
- Serviços de usuário.
- Integração Pluggy.
- Sincronização de contas e transações.
- Tratamento de exceções.
- Segurança JWT.

---

## Diferenciais técnicos

Este projeto vai além de um CRUD simples. Ele inclui recursos importantes para aplicações reais:

- Autenticação JWT.
- Controle de acesso por usuário autenticado.
- Integração com API financeira externa.
- Sincronização de dados externos para base local.
- Paginação de transações.
- Resiliência em chamadas HTTP externas.
- Retry e circuit breaker com Resilience4j.
- Tratamento global de exceções.
- Respostas de erro padronizadas.
- Validação de entrada com Bean Validation.
- Separação clara entre controller, service, repository e DTO.
- Frontend com rotas protegidas.
- Uso de services Angular para centralizar comunicação com API.
- Uso de interceptors para envio automático do token JWT.
- Conteinerização com Docker para padronização do ambiente.
- Testes automatizados para dar mais segurança à evolução do projeto.
- Swagger/OpenAPI para documentação interativa da API.
- Observabilidade com Spring Boot Actuator.
- Métricas expostas via Micrometer.
- Endpoint Prometheus para coleta de métricas.
- Health checks para acompanhamento do status da aplicação.
- Estrutura preparada para crescimento do domínio.

---

## Observabilidade

O projeto possui suporte a observabilidade com Spring Boot Actuator e Micrometer.

Recursos disponíveis:

- Health check da aplicação.
- Informações gerais da aplicação.
- Métricas de requisições HTTP.
- Métricas de JVM.
- Métricas de memória.
- Métricas de threads.
- Métricas de CPU.
- Métricas de retry e circuit breaker.
- Exportação de métricas no formato Prometheus.

Endpoints principais:

```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
GET /actuator/metrics/{requiredMetricName}
GET /actuator/prometheus
```

---

## Segurança

O projeto utiliza autenticação baseada em JWT.

Fluxo básico:

1. O usuário realiza login em `/auth/login`.
2. A API retorna um token JWT.
3. O frontend armazena o token.
4. O interceptor Angular envia o token nas requisições protegidas.
5. O backend valida o token com Spring Security e OAuth2 Resource Server.

Boas práticas aplicadas ou previstas:

- Senhas criptografadas com BCrypt.
- Endpoints protegidos por autenticação.
- DTOs validados com Bean Validation.
- Tratamento de credenciais inválidas.
- Tratamento de token ausente, inválido ou expirado.
- Arquivo `.env` ignorado pelo Git.
- Uso de `.env.example` sem credenciais reais.

---

## Aprendizados demonstrados

Durante o desenvolvimento deste projeto, foram aplicados conceitos importantes de engenharia de software:

- Modelagem de entidades relacionais.
- Autenticação e autorização com JWT.
- Proteção de endpoints com Spring Security.
- Integração com serviços externos usando REST Client.
- Tratamento global de exceções com respostas padronizadas.
- Tratamento de falhas em chamadas HTTP externas.
- Resiliência com retry e circuit breaker usando Resilience4j.
- Separação de responsabilidades entre controller, service, repository, DTO, entities, security, config e exceptions.
- Consumo de API REST no frontend com Angular HttpClient.
- Proteção de rotas no frontend com guards.
- Uso de interceptors para envio automático do token JWT.
- Sincronização de dados externos para persistência local.
- Paginação de transações financeiras.
- Observabilidade com Spring Boot Actuator.
- Exposição de métricas com Micrometer.
- Documentação da API com Swagger/OpenAPI.
- Testes automatizados com JUnit 5 e Mockito.
- Conteinerização com Docker.
- Organização de projeto full stack.
- Evolução incremental de funcionalidades.

## Status do projeto

Projeto em desenvolvimento.

A aplicação já possui autenticação JWT, integração com Pluggy, Swagger/OpenAPI, Docker, testes automatizados e observabilidade com Actuator/Micrometer.

A próxima etapa recomendada é evoluir a aplicação para um ambiente mais próximo de produção com PostgreSQL, CI/CD, logs estruturados, dashboards e deploy em cloud.

---

## Autor

Desenvolvido por José Junior.

GitHub: [josejunior30](https://github.com/josejunior30)

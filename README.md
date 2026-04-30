# Cadastro

Aplicação full stack para cadastro de usuários, autenticação com JWT e integração com a Pluggy para conexão bancária, sincronização de contas e consulta de transações financeiras.

O projeto foi desenvolvido com foco em uma arquitetura backend organizada em camadas, frontend moderno com Angular e integração com API externa, demonstrando domínio de autenticação, consumo de APIs, persistência de dados e separação de responsabilidades.

---

## Visão geral

Este projeto demonstra competências práticas em desenvolvimento full stack, incluindo:

- Construção de API REST com Java e Spring Boot.
- Autenticação baseada em JWT.
- Organização backend em camadas: controller, service, repository, DTO, entities, security, config e exceptions.
- Integração com API externa da Pluggy.
- Sincronização de contas e transações financeiras.
- Uso de Spring Security, OAuth2 Resource Server e OAuth2 Client.
- Persistência com Spring Data JPA.
- Resiliência em chamadas externas com Resilience4j, retry e circuit breaker.
- Frontend Angular com rotas protegidas, services, interceptors e guards.
- Consumo de backend via HttpClient.
- Separação entre ambiente, serviços, modelos e páginas no frontend.

---

## Funcionalidades

### Backend

- Login de usuários com geração de token JWT.
- CRUD de usuários.
- Proteção de endpoints autenticados.
- Criação de connect token da Pluggy.
- Sincronização de item Pluggy.
- Listagem de contas bancárias vinculadas ao usuário autenticado.
- Listagem paginada de transações por conta.
- Tratamento de erros de integração.
- Logs para acompanhamento de autenticação e sincronização.
- Cache temporário da API key da Pluggy.
- Retry e circuit breaker para chamadas externas.
-  Testes automatizados para validação da aplicação

### Frontend

- Tela de login.
- Armazenamento do token JWT no navegador.
- Validação de autenticação pelo payload do token.
- Rotas protegidas com guard.
- Integração com Pluggy Connect.
- Navegação para contas e transações.
- Services dedicados para autenticação e Pluggy.
- Estrutura preparada para evolução de telas e componentes.

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
cadastro/
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
│       ├── src/test/java/com/junior/cadastro/
│       │   ├── controller/
│       │   ├── security/
│       │   ├── service/
│       │   
│       └── pom.xml
│
└── front-end/
    └── cadastro/
        ├── src/app/
        │   ├── guard/
        │   ├── interceptors/
        │   ├── models/
        │   ├── pages/
        │   ├── services/
        │   ├── app.routes.ts
        │   └── app.config.ts
        └── package.json
```

---

## Principais endpoints da API

### Autenticação

```http
POST /auth/login
```

Realiza login do usuário e retorna um token JWT.

### Usuários

```http
GET /user
GET /user/{id}
POST /user
PUT /user/{id}
DELETE /user/{id}
```

Endpoints para gerenciamento de usuários.

### Pluggy

```http
POST /pluggy/connect-token
POST /pluggy/items/sync
GET /pluggy/accounts
GET /pluggy/accounts/{accountId}/transactions
```

Endpoints para integração com a Pluggy, conexão bancária, sincronização de contas e consulta de transações.

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

---

## Executando o backend

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

## Executando o frontend

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

## Variáveis de ambiente

O backend utiliza credenciais da Pluggy e configurações de autenticação. Recomenda-se configurar essas informações fora do código-fonte, usando variáveis de ambiente ou perfis do Spring.

Exemplo:

```properties
pluggy.base-url=https://api.pluggy.ai
pluggy.client-id=${PLUGGY_CLIENT_ID}
pluggy.client-secret=${PLUGGY_CLIENT_SECRET}
```

---

## Diferenciais técnicos

Este projeto vai além de um CRUD simples. Ele inclui recursos importantes para aplicações reais:

- Autenticação JWT.
- Controle de acesso por usuário autenticado.
- Integração com API financeira externa.
- Sincronização de dados externos para base local.
- Paginação de transações.
- Resiliência em chamadas HTTP externas.
- Separação clara entre controller, service, repository e DTO.
- Frontend com rotas protegidas.
- Uso de services Angular para centralizar comunicação com API.
- Conteinerização com Docker para padronização do ambiente
- Testes automatizados para dar mais segurança à evolução do projeto
- Estrutura preparada para crescimento do domínio
- Estrutura preparada para crescimento do domínio.

---

## Melhorias planejadas

### Infraestrutura e DevOps

- [ ] Substituir H2 por PostgreSQL em ambiente de desenvolvimento e produção.
- [ ] Criar profiles do Spring para `dev` e `prod`.
- [ ] Adicionar pipeline de CI/CD com GitHub Actions.
- [ ] Executar testes automaticamente a cada pull request.
- [ ] Gerar build automatizado do frontend e backend.
- [ ] Publicar imagens Docker em registry.

### Backend

- [ ] Implementar webhooks da Pluggy para receber eventos automaticamente.
- [ ] Criar endpoint seguro para processamento de webhook.
- [ ] Validar assinatura ou origem dos webhooks.
- [ ] Melhorar tratamento global de exceções com `@ControllerAdvice`.
- [ ] Adicionar documentação da API com Swagger/OpenAPI.
- [ ] Melhorar regras de autorização por perfil de usuário.
- [ ] Adicionar refresh token.
- [ ] Persistir histórico de sincronizações.
- [ ] Criar auditoria de ações sensíveis.
- [ ] Melhorar observabilidade com métricas e health checks.
- [ ] Adicionar logs estruturados.

### Frontend

- [ ] Melhorar layout das telas com identidade visual consistente.
- [ ] Criar dashboard financeiro com resumo de contas e transações.
- [ ] Adicionar filtros por data, categoria e valor.
- [ ] Criar tela de detalhes da conta.
- [ ] Criar feedback visual para loading, erro e sucesso.
- [ ] Implementar refresh automático após sincronização.
- [ ] Criar testes de componentes.
- [ ] Melhorar interceptors para tratamento global de erros HTTP.
- [ ] Melhorar responsividade mobile.

### Segurança

- [ ] Remover qualquer configuração sensível do código.
- [ ] Adicionar rate limit em endpoints de autenticação.
- [ ] Adicionar validações mais fortes nos DTOs.
- [ ] Revisar política de CORS.
- [ ] Implementar proteção contra brute force no login.
- [ ] Melhorar armazenamento de tokens no frontend.
- [ ] Documentar fluxo de autenticação.

### Qualidade e documentação

- [ ] Criar coleção Postman ou Insomnia.
- [ ] Adicionar exemplos de payloads da API.
- [ ] Criar diagrama simples da arquitetura.
- [ ] Adicionar screenshots do frontend.
- [ ] Adicionar instruções completas de setup da Pluggy.
- [ ] Adicionar seção de troubleshooting.
- [ ] Padronizar commits com Conventional Commits.
- [ ] Adicionar licença ao projeto.

---

## Roadmap sugerido

### Versão 1.0

- Login funcional.
- Cadastro e gerenciamento de usuários.
- Integração com Pluggy Connect.
- Sincronização manual de contas e transações.
- Listagem de contas e transações no frontend.

### Versão 1.1

- Webhooks da Pluggy.
- Docker e Docker Compose.
- PostgreSQL.
- Swagger/OpenAPI.
- Testes automatizados.

### Versão 1.2

- Dashboard financeiro.
- Filtros avançados.
- Observabilidade.
- CI/CD.
- Deploy em ambiente cloud.

---

## Aprendizados demonstrados

Durante o desenvolvimento deste projeto, foram aplicados conceitos importantes de engenharia de software:

- Modelagem de entidades relacionais.
- Autenticação e autorização.
- Integração com serviços externos.
- Tratamento de falhas em chamadas HTTP.
- Separação de responsabilidades.
- Consumo de API REST no frontend.
- Proteção de rotas.
- Organização de projeto full stack.
- Evolução incremental de funcionalidades.

---

## Autor

Desenvolvido por José Junior.

GitHub: [josejunior30](https://github.com/josejunior30)

---

## Status do projeto

Projeto em desenvolvimento.

A próxima etapa recomendada é evoluir a aplicação para um ambiente mais próximo de produção,  PostgreSQL, webhooks, documentação OpenAPI e pipeline de CI/CD.

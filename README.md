# AcessAPI - API de Gerenciamento de Usuários e Controle de Acesso

Este projeto é uma API RESTful desenvolvida com Spring Boot, focada em gerenciamento de usuários e controle de acesso baseado em roles (RBAC). Ele oferece funcionalidades como registro de usuários, autenticação, criação de roles e gerenciamento de perfis de usuário, com autorização granular para diferentes tipos de usuários.

## Funcionalidades

*   **Autenticação de Usuários:** Login com JWT (JSON Web Tokens).
*   **Registro de Usuários:** Criação de novas contas de usuário (restrito a administradores).
*   **Gerenciamento de Roles:** Criação de novas roles (restrito a administradores).
*   **Listagem de Usuários:** Visualização de todos os usuários cadastrados (restrito a administradores).
*   **Visualização de Perfil:** Usuários podem ver seus próprios perfis; administradores podem ver qualquer perfil.
*   **Atualização de Perfil:** Usuários podem atualizar suas próprias senhas; administradores podem atualizar username, senha e role de qualquer usuário.
*   **Controle de Acesso:** Autorização baseada em roles (`ROLE_ADMIN`, `ROLE_USER`).

## Tecnologias Utilizadas

*   **Spring Boot:** Framework para desenvolvimento rápido de aplicações Java.
*   **Spring Security:** Para autenticação e autorização.
*   **Spring Data JPA:** Para persistência de dados com Hibernate.
*   **MySQL:** Banco de dados relacional.
*   **Lombok:** Para reduzir código boilerplate.
*   **Docker & Docker Compose:** Para orquestração e execução do ambiente.

## Como Rodar o Projeto com Docker

Para colocar o projeto em funcionamento utilizando Docker e Docker Compose, siga os passos abaixo:

### Pré-requisitos

Certifique-se de ter o Docker e o Docker Compose instalados em sua máquina.

*   [Instalar Docker](https://docs.docker.com/get-docker/)
*   [Instalar Docker Compose](https://docs.docker.com/compose/install/)

### Passos

1.  **Clone o Repositório:**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO>
    cd AcessAPI
    ```
    (Substitua `<URL_DO_SEU_REPOSITORIO>` pela URL real do seu repositório Git.)

2.  **Configure o Ambiente:**
    O arquivo `docker-compose.yml` já está configurado para subir o serviço da API e um banco de dados MySQL.
    Certifique-se de que as configurações de banco de dados em `src/main/resources/application.properties` (dentro do projeto Spring Boot) correspondam às do `docker-compose.yml`.

    Exemplo de `application.properties` (verifique se `mysql:3306` está correto para o serviço do Docker):
    ```properties
    spring.datasource.url=jdbc:mysql://mysql:3306/acessapi_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
    spring.datasource.username=user
    spring.datasource.password=password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    ```

3.  **Suba os Contêineres Docker:**
    No diretório raiz do projeto (`AcessAPI`), execute o seguinte comando:
    ```bash
    docker-compose up --build
    ```
    Este comando irá construir a imagem Docker da sua aplicação (se necessário), criar e iniciar os contêineres para a API e o MySQL.

    Para rodar em segundo plano, use:
    ```bash
    docker-compose up -d --build
    ```

4.  **Verifique os Logs:**
    Você pode acompanhar os logs dos serviços para garantir que tudo está funcionando corretamente:
    ```bash
    docker-compose logs -f
    ```

A API estará disponível em `http://localhost:8080` (ou na porta configurada no `application.properties`).

## Usuário Administrador Padrão

Para facilitar o início e os testes, um usuário administrador padrão é criado automaticamente no banco de dados na primeira inicialização da aplicação (devido a `spring.jpa.hibernate.ddl-auto=update` e a lógica de inicialização de roles/usuários, se presente).

**Credenciais do Administrador:**

*   **Username:** `admin`
*   **Password:** `admin`

**ATENÇÃO:** Por questões de segurança, é **altamente recomendável** alterar a senha deste usuário administrador imediatamente após o primeiro login em um ambiente de produção.

## Endpoints da API

Aqui estão os principais endpoints disponíveis:

*   `POST /api/auth/login` - Autentica um usuário e retorna um JWT.
*   `POST /api/auth/users` - Registra um novo usuário (requer `ROLE_ADMIN`).
*   `POST /api/roles` - Cria uma nova role (requer `ROLE_ADMIN`).
*   `GET /api/auth/users` - Lista todos os usuários (requer `ROLE_ADMIN`).
*   `GET /api/auth/users/{id}` - Obtém detalhes de um usuário por ID (requer `ROLE_ADMIN` ou que o `id` seja o do próprio usuário autenticado).
*   `PUT /api/auth/me` - Atualiza a senha do usuário autenticado.
*   `PUT /api/auth/users/{id}` - Atualiza os dados de um usuário por ID (requer `ROLE_ADMIN` ou que o `id` seja o do próprio usuário autenticado).

## Observação sobre Testes e Conectividade com o Banco de Dados

Durante a fase de testes automatizados (executados via `mvnw clean install`), pode ocorrer um erro de `Communications link failure` relacionado à conexão com o banco de dados MySQL (`mysql:3306`). Este é um problema de ambiente de teste e não afeta a funcionalidade da aplicação quando rodada via Docker Compose, onde o serviço `mysql` é acessível.

Para resolver este problema nos testes, certifique-se de que o serviço MySQL esteja acessível a partir do ambiente onde os testes são executados, ou configure um banco de dados de teste em memória (como H2) para o perfil de teste.

---

Desenvolvido com ❤️ por [Seu Nome/Sua Equipe]

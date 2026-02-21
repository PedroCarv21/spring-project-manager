# spring-project-manager

## Tecnologias utilizadas

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
## Descrição

Aplicação criada para o gerenciamento de projetos através de uma API REST, sendo cada projeto coordenado por um único administrador e organizado em times e tarefas, onde os times podem incluir gerentes e/ou membros, cada um com regras de acesso e responsabilidades específicas. A API contém autenticação via Spring Security e persistência em PostgreSQL.
### Papeis dos usuários

Nesta aplicação pode haver três tipos de usuários:

| Papeis  | Função                                                                                                                       |
| ------- | ---------------------------------------------------------------------------------------------------------------------------- |
| Admin   | É o criador do projeto, tendo acesso total<br>ao seu gerenciamento                                                           |
| Manager | Pode gerenciar parcialmente o time em<br>que se encontra e consultar dados do<br>projeto ao qual se encontra.                |
| Member  | Terá acesso somente a consulta de <br>informações do seu projeto e também a<br>atualização da tarefa ao qual está vinculado. |

## Arquitetura do projeto

### Arquitetura em camadas

A arquitetura do projeto foi divida nas seguintes camadas:

- **Controller:** é responsável por expor os endpoints da aplicação.
- **Service:** contém a lógica de negócio da aplicação.
- **Repository:** possui os métodos CRUD para a persistência de dados no banco.
- **Entity:** representa o mapeamento das tabelas do banco de dados.
- **DTO:** utilizado para transferência de dados entre cliente e servidor.
- **Mapper:** responsável pela conversão entre entidades e DTOs (utilizando MapStruct).
- **Security:** configuração de autenticação e autorização com Spring Security.
- **Exception Handler:** tratamento global de exceções da aplicação.
## Como executar o projeto

### Baixando a aplicação

- Clique no botão verde `<> Code ▼` → Clique em `HTTPS` → Copie a URL.
- Abra o Git Bash → Execute o comando `git clone <URL copiada>` → Entre na pasta da aplicação.

### Gerando o JAR e rodando a aplicação

Executa os seguintes comandos no prompt:
- `mvnw.cmd clean package -DskipTests`
- `docker compose up -d --build`

Agora acesse `localhost:8080` no seu navegador e aguarde um tempo para a página aparecer. Essa será a tela inicial:

![Tela de Login](TELA_LOGIN.png)

Clique em `Cadastra-se` e crie uma nova conta. Por fim, você será enviado para a página do Swagger, onde estão os endpoints.

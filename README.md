# blog-backend  [![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/) [![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/) [![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/) ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

Blog Project for ATRI Institute

![Project Status](https://img.shields.io/badge/status-under%20development-green) &nbsp; [![GitHub License](https://img.shields.io/github/license/institute-atri/blog-backend?color=blue)](https://github.com/institute-atri/blog-backend/blob/main/LICENSE) &nbsp; [![Java](https://img.shields.io/badge/Java-21.0-orange)](https://www.java.com/) &nbsp; [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)](https://spring.io/projects/spring-boot)


Our project is a blogging system in the development phase, developed with Java 21 and Spring Boot. With a strong focus on security and usability, we are incorporating JWT tokens for secure authentication and implementing Spring Security to protect our endpoints. We use efficient DTOs and mappings to optimize communication and data manipulation, making the management of posts, categories, tags and users more intuitive. For storage and flexibility, we are integrating MongoDB as our main database. In addition, we are working to create clear and interactive documentation with Swagger, making it easier to understand and use our APIs.


## 🍃 Back-End Engineering

| [<img src="https://github.com/rafael-dev2021.png?size=115" width=115><br><sub>@Rafael-Dev</sub>](https://github.com/rafael-dev2021) |    [<img src="https://github.com/NicollyRamos.png?size=115" width=115><br><sub>@NicollyRamos</sub>](https://github.com/NicollyRamos)     | [<img src="https://github.com/wendoxx.png?size=115" width=115><br><sub>@wendoxx</sub>](https://github.com/wendoxx) |
|:---------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------:|
|            **Rafael Silva** <br> *Back-end Engineer* <br> [LinkedIn](https://www.linkedin.com/in/rafael-s-a79314207/)             | **Nicolly Ramos** <br> *Back-end Engineer* <br> [LinkedIn](https://www.linkedin.com/in/nicolly-ramos/) |            **Wendel Silva** <br> *Back-end Engineer* <br> [LinkedIn](https://www.linkedin.com/in/wendel-da-silva-martins-9ba630265/)             |

## Pre-requisites
- Before you start, make sure you meet the following requirements:
- Java 21 installed
- Windows updated
- Linux updated
- macOS updated

## Installation
1. Clone the repository: 
```bash
git clone https://github.com/institute-atri/blog-backend.git
```

## Technology used
 - [IDE-InteliJ](https://www.jetbrains.com/pt-br/idea/download/?section=windows)
 - [JDK-Java21](https://download.oracle.com/java/21/archive/jdk-21_windows-x64_bin.exe)
 - [MongoDB](https://www.mongodb.com/try/download/shell)

## API endpoints
```bash
   -> AuthenticationController

   - POST /v1/auth/login - Authenticate user by verifying credentials.

   - POST /v1/auth/register - Endpoint for user registration.

   - POST /v1/auth/logout - Endpoint for logging out the current user.

   - POST /v1/auth/refresh-token - Endpoint to refresh the access token using a valid refresh token.

   ```
<hr/>

```bash
   -> UserController

   - GET - /v1/users - Returns a list of all users registered in the system, only with the admin token.

   - GET - /v1/users/find/{id} - Returns the user with the specified ID. Only users with the ADMIN role can search by user.

   - PUT - /v1/users/update/{id} - Updates the user with the specified ID.
 
   - DELETE - /v1/users/delete/{id} - Deletes the user with the specified ID. Only users with the ADMIN role can delete the user.

   - GET - /v1/users/posts/id - Returns posts by user id.

   - POST - /v1/users/change-password - Endpoint to change the password of the currently authenticated user.

   ```
<hr/>

```bash
  -> PostController

  - GET - /v1/posts - Collection of events.

  - GET - /v1/posts/find/{id} - Event identifier ID.

  - POST - /v1/posts/create - Create an event.

  - PUT - /v1/posts/update/{id} - Update an event by ID.

  - DELETE - /v1/posts/delete/{id} - Delete an event by ID.
  ```
<hr/>

```bash
  -> CategoryController

  - GET - /v1/categories - Collection of events.

  - GET - /v1/categories/posts/{id} - Returns posts by category ID.

  - GET - /v1/categories/find/{id} - Event identifier ID.

  - POST - /v1/categories/create - Create an event.

  - PUT - /v1/categories/update/{id} - Update an event by ID.

  - DELETE - /v1/categories/delete/{id} - Delete an event by ID.
  ```
<hr/>

```bash
  -> TagController

  - GET - /v1/tags - Collection of events.

  - GET - /v1/tags/posts/{id} - Returns posts by tag ID.

  - GET - /v1/tags/find/{id} - Event identifier ID.

  - POST - /v1/tags/create - Create an event.

  - PUT - /v1/tags/update/{id} - Update an event by ID.

  - DELETE - /v1/tags/delete/{id} - Delete an event by ID.
  ```
<hr/>

# Authentication
The API uses Spring Security for authentication control. The following roles are available:
```bash
USER -> Standard user role for logged-in users.
ADMIN -> Admin role for managing partners (registering new partners)
```

## Dependendcies used
- Spring web
- Lombok
- Spring boot dev tools
- Spring data mongoDb
- Spring security
- Hibernate validator
- Java jwt auth0
- Spring doc open api
- Mapstruct
- Junit
- Mockito
- Cucumber

## Contributing
#### If you have any suggestions for improving this project, follow these steps: 
  1. Bifurcate the project
  2. Create a branch `git checkout -b feat-#1/sugestions`
  3. Make your changes to the code
  4. Confirm your changes `git commit -m 'feat: add a new susgestion (#1)'`
  5. Send to branch `git push origin feat-#1/sugestions`
  6. Open pull request
#### Don't forget to read `CONTRIBUTING.md` for details on the code of conduct and the process of sending pull requests to us. 

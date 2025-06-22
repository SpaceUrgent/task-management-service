# Task Management Service
***

A full-stack task management application built with a **hexagonal (ports & adapters) architecture** and **Domain-Driven Design (DDD)** principles for the backend, and a modern React-based frontend.

This project is designed for extensibility, maintainability, and testability, making it easy to adapt to new requirements or technologies. The backend cleanly separates business logic from infrastructure, while the frontend provides a responsive and user-friendly interface for managing tasks, projects, and users.

## Application Use Cases
***

### User Management
- **User Registration:** Register a new user with email, first name, last name, and password.
- **Profile Management:** View and update user profile information (first name, last name, password).

### Project Management
- **Project Listing:** Retrieve all projects available to the current user.
- **Project Details:** View detailed information about a specific project.
- **Project Creation:** Create a new project with title and description.
- **Update project profile:** 
  - Update project info, manage task statuses chain for available for current project.
  - Add members and manage their roles.

### Task Management
- **Task Listing:** List tasks for a project, with support for pagination and filtering.
- **Task Details:** View detailed information about a specific task.
- **Task management:** 
  - Create a new task within a project, assign it to a user, set priority, status, and due date.
  - Update task details, status, including title, description, assignee, status, priority, and due date.
- **Task Comments:** Add and list comments to a task.
- **Task change logs:** Review task change logs to see all actions performed over each task

### Dashboard
- **Assigned Tasks Dashboard:** View a summary and paginated list of tasks assigned to the current user, including overdue tasks.
- **Owned Tasks Dashboard:** View a summary and paginated list of tasks owned by the current user, including overdue tasks.

All use cases are protected by access control and validation, ensuring that only authorized users can perform actions on projects and tasks they are members of.


## Project Structure

```
.
├── adapter/
│   ├── in/                  # Inbound adapters (e.g., web API)
│   └── out/                 # Outbound adapters (e.g., persistence, encryption)
├── application/             # Application layer (use cases, services)
├── config/                  # Spring configuration module
├── deploy/                  # Docker and deployment files
├── domain/                  # Domain layer (entities, aggregates, domain services)
├── launcher/                # Application entrypoint (Spring Boot)
├── ui/                      # Frontend (React)
└── pom.xml                  # Maven parent configuration
```

The project is organized according to the hexagonal architecture (ports & adapters), with clear separation between core business logic, application services, and infrastructure concerns. The frontend is located in the `ui/` directory and communicates with the backend via REST APIs. 

## Hexagonal Architecture Diagram

The backend is structured according to the hexagonal architecture (also known as ports and adapters), which separates the core domain logic from external concerns such as databases, web APIs, and encryption. This makes the system highly maintainable and adaptable to change.

## Modules & Technologies

### Backend
- **domain**: Pure business logic, aggregates, value objects (Java 21)
- **application**: Use cases, application services (Java 21, Jakarta Validation)
- **adapter/in/web**: REST API (Spring Boot, Spring Security, Spring Validation)
- **adapter/out/persistence-jpa**: JPA persistence (Spring Data JPA, PostgreSQL)
- **adapter/out/password-encryption**: Password hashing (Spring Security Crypto)
- **config/spring-config**: Centralized Spring configuration (Spring Boot, Testcontainers)
- **launcher/spring-launcher**: Application entrypoint (Spring Boot)

### Frontend
- **ui**: React 19, React Router, Bootstrap 5, Axios

## Installation & Running

### Prerequisites
- Docker & Docker Compose installed

### Backend Only

```bash
cd deploy
docker-compose -f docker-compose-spring.yml up --build
```

### Full Stack (Backend + Frontend + DB)

```bash
cd deploy
docker-compose -f docker-compose-spring-ui.yml up --build
```

## Additional Information

- **Hexagonal Architecture**: The backend is structured to separate business logic (domain, application) from infrastructure (adapters), making it easy to swap out technologies or add new interfaces.
- **Testing**: Uses JUnit, Mockito, and Testcontainers for robust testing.
- **Security**: Passwords are securely hashed using Spring Security Crypto.
- **Configuration**: All environment variables and configuration are managed via the `config/spring-config` module and Docker Compose files. 


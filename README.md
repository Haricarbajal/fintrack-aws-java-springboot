# finTrack - Personal Finance Management API

<div align="center">

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-Cloud-FF9900?style=for-the-badge&logo=amazonwebservices&logoColor=white)

**A RESTful API for personal finance tracking built with Spring Boot, featuring JWT authentication, AI-powered insights, and AWS cloud deployment.**

[Features](#features) · [Tech Stack](#tech-stack) · [API Endpoints](#api-endpoints) · [Getting Started](#getting-started) · [Architecture](#architecture) · [Roadmap](#roadmap)

</div>

---

## Features

- **User Authentication** — Secure registration and login with JWT tokens and BCrypt password hashing
- **Transaction Management** — Full CRUD for income and expense tracking with category support
- **User Isolation** — Each user can only access their own data, enforced at every layer
- **Input Validation** — Request validation with Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Size`)
- **Global Error Handling** — Centralized exception handling with `@ControllerAdvice` and consistent error responses
- **DTO Pattern** — Response DTOs to prevent sensitive data exposure (e.g., user passwords)
- **Structured Logging** — SLF4J logging across controllers, services, and exception handlers

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 25 |
| **Framework** | Spring Boot 4.1.0 |
| **Security** | Spring Security + JWT (jjwt 0.12.6) |
| **Database** | MySQL 8.0 + Spring Data JPA / Hibernate |
| **Validation** | Jakarta Bean Validation |
| **Logging** | SLF4J + Logback (via Lombok `@Slf4j`) |
| **Build** | Maven |

---

## Architecture

```
com.hari.finTrack
├── controller/          # REST endpoints (Auth, Transaction)
├── dto/                 # Request/Response objects (no entity exposure)
├── exception/           # Custom exceptions + Global handler
├── model/               # JPA entities (User, Transaction, TipoTransaccion)
├── repository/          # Spring Data JPA interfaces
├── security/            # JWT filter, config, utilities
├── service/             # Business logic layer
└── FinTrackApplication  # Entry point
```

### Security Flow

```
┌──────────┐     POST /api/auth/login      ┌──────────────┐
│  Client  │ ──────────────────────────────>│ AuthController│
│          │ <──────────────────────────────│  (JWT Token) │
└──────────┘     { token, email, nombre }   └──────────────┘
     │
     │  GET /api/transactions
     │  Authorization: Bearer <token>
     ▼
┌──────────────────┐    ┌───────────────┐    ┌─────────────────────┐
│ JwtAuthFilter    │───>│ SecurityContext│───>│ TransactionController│
│ (validate token) │    │ (UserPrincipal)│    │ (user-scoped data)  │
└──────────────────┘    └───────────────┘    └─────────────────────┘
```

---

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/register` | Register a new user | `201 Created` |
| `POST` | `/api/auth/login` | Login and receive JWT | `200 OK` |

**Register Request:**
```json
{
  "email": "user@example.com",
  "nombre": "John Doe",
  "password": "securePass123"
}
```

**Auth Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "nombre": "John Doe"
}
```

### Transactions (Protected — requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/transactions` | List all user transactions | `200 OK` |
| `GET` | `/api/transactions/{id}` | Get a specific transaction | `200 OK` |
| `POST` | `/api/transactions` | Create a new transaction | `201 Created` |
| `PUT` | `/api/transactions/{id}` | Update a transaction | `200 OK` |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction | `204 No Content` |

**Transaction Request:**
```json
{
  "descripcion": "Grocery shopping",
  "monto": 45.50,
  "tipo": "GASTO",
  "fecha": "2026-04-28",
  "categoria": "Food"
}
```

**Transaction Response:**
```json
{
  "id": 1,
  "descripcion": "Grocery shopping",
  "monto": 45.50,
  "tipo": "GASTO",
  "fecha": "2026-04-28",
  "categoria": "Food"
}
```

### Error Responses

| Status | Description | Example |
|--------|-------------|---------|
| `401 Unauthorized` | Invalid credentials | `{"error": "Invalid credentials", "status": "401"}` |
| `404 Not Found` | Resource doesn't exist | `{"error": "Transaction not found: 5", "status": "404"}` |
| `409 Conflict` | Duplicate resource | `{"error": "Email already exists", "status": "409"}` |

---

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+
- MySQL 8.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Haricarbajal/fintrack-aws-java-springboot.git
   cd fintrack-aws-java-springboot
   ```

2. **Configure the database**

   Create a MySQL database (or let the app auto-create it):
   ```sql
   CREATE DATABASE fintrack_db;
   ```

3. **Set environment variables**
   ```bash
   export DB_USERNAME=your_db_user
   export DB_PASSWORD=your_db_password
   export JWT_SECRET=your-secret-key-at-least-256-bits
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Test the API**
   ```bash
   # Register
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"test@email.com","nombre":"Test User","password":"password123"}'

   # Use the returned token for authenticated requests
   curl -X GET http://localhost:8080/api/transactions \
     -H "Authorization: Bearer <your-token>"
   ```

---

## Roadmap

### Phase 1: Professional Backend Foundations
- [x] Input validation (`@Valid`, `@NotBlank`, `@Email`, `@Size`)
- [x] Custom exceptions (`ResourceNotFoundException`, `DuplicateResourceException`, `UnauthorizedException`)
- [x] Global error handling (`@ControllerAdvice`)
- [x] Response DTOs (prevent entity/sensitive data exposure)
- [x] Structured logging with SLF4J
- [ ] CORS configuration
- [ ] Environment variables for secrets

### Phase 2: Advanced Features
- [ ] Transaction filtering (by date, category, type, amount range)
- [ ] Pagination and sorting (`Pageable`, `Page<T>`)
- [ ] Reports endpoint (totals by category, monthly balance)
- [ ] Category management (dedicated entity with CRUD)
- [ ] Recurring transactions (fixed monthly expenses)
- [ ] CSV export

### Phase 3: Testing
- [ ] Unit tests (JUnit 5 + Mockito)
- [ ] Repository integration tests (`@DataJpaTest`)
- [ ] Controller integration tests (`@WebMvcTest`)
- [ ] Security tests (protected endpoints, invalid tokens)

### Phase 4: AI Integration (Claude API)
- [ ] Automatic transaction categorization with AI
- [ ] Intelligent spending analysis (patterns, alerts)
- [ ] AI-generated monthly financial summary
- [ ] Personalized savings suggestions

### Phase 5: AWS Cloud Infrastructure
- [ ] Dockerize application (Dockerfile + docker-compose)
- [ ] Amazon RDS for MySQL
- [ ] Deploy on EC2 / ECS Fargate
- [ ] S3 for exported reports
- [ ] Secrets Manager for credentials
- [ ] CloudWatch for logging and monitoring

### Phase 6: DevOps & CI/CD
- [ ] GitHub Actions CI pipeline (build + automated tests)
- [ ] Automated CD to AWS
- [ ] Spring profiles (dev, staging, prod)
- [ ] API documentation with Swagger/OpenAPI

---

## License

This project is for educational and portfolio purposes.

---

<div align="center">

Built with Java + Spring Boot

</div>

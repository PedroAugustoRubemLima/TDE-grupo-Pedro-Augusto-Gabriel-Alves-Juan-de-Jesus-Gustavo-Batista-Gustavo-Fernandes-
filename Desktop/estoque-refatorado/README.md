# Dairy Inventory & Sales Management System

A full-stack inventory and sales management system built for a real dairy products company, currently in production.

## Overview

This system manages the complete operational flow of a dairy business: product catalog with expiration tracking, stock control, purchase entries, sales pipeline with multi-item orders, client and employee management, and automated PDF reporting.

## Features

- **Product Management** — full catalog with type, price, quantity, expiration dates and photo gallery
- **Inventory Control** — real-time stock tracking with automatic debit on sales and credit on purchases; validates stock availability before confirming any sale
- **Sales Pipeline** — multi-item orders with automatic total calculation, client and employee association, and history with date-range filtering
- **Purchase Entries** — records stock entries with automatic inventory update
- **Client & Employee Management** — full CRUD with soft delete (inactivation)
- **PDF Reports** — automated sales reports generated server-side with Apache PDFBox
- **Secure Authentication** — JWT stateless auth with BCrypt password hashing and brute-force protection (5 attempts / 15 min lockout per user + IP)
- **Endpoint Protection** — all API routes require valid JWT; only `/api/auth/**` and static files are public

## Security

| Feature | Implementation |
|---|---|
| Authentication | JWT (HS256) — stateless, no server-side session |
| Password hashing | BCrypt (Spring Security) |
| Brute-force protection | 5 attempts → 15 min lockout, per user+IP |
| AES encryption | Key loaded from environment variable — never hardcoded |
| CORS | Configurable via environment variable |
| Input validation | `@Valid` + Bean Validation on all request bodies |
| Error handling | Global `@RestControllerAdvice` — no stack traces exposed |

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3.2
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- JJWT 0.11.5
- Apache PDFBox 3.0

**Frontend**
- HTML5 / CSS3 / JavaScript (vanilla)
- Static files served by Spring Boot

## Environment Variables

Copy `.env.example` to `.env` and fill in your values:

```bash
DB_USER=root
DB_PASS=your_database_password

# Min 32 chars — generate with: openssl rand -base64 48
JWT_SECRET=your-long-random-jwt-secret

# Exactly 16 characters
AES_SECRET=YourAesKey123456

CORS_ALLOWED_ORIGINS=http://localhost:8080
```

## Getting Started

**Prerequisites:** Java 21+, Maven 3.9+, MySQL 8+

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/estoque-laticinios.git
cd estoque-laticinios

# 2. Create the database
mysql -u root -p -e "CREATE DATABASE estoque_jb;"

# 3. Set environment variables (or edit application.properties for dev)
export DB_USER=root
export DB_PASS=yourpassword
export JWT_SECRET=your-secret-key-at-least-32-chars
export AES_SECRET=ExactlySixteenC1

# 4. Run
mvn spring-boot:run
```

Access: `http://localhost:8080`

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Login → returns JWT |
| GET | `/api/auth/validate` | JWT | Validate token |
| GET | `/api/produtos` | JWT | List products |
| POST | `/api/produtos` | JWT | Create product |
| DELETE | `/api/produtos/{id}` | JWT | Inactivate product |
| GET | `/api/estoque` | JWT | List stock |
| POST | `/api/estoque/retirar` | JWT | Remove from stock |
| POST | `/api/estoque/adicionar` | JWT | Add to stock |
| GET | `/api/clientes` | JWT | List clients |
| POST | `/api/clientes` | JWT | Create client |
| GET | `/api/funcionarios` | JWT | List employees |
| POST | `/api/funcionarios` | JWT | Create employee |
| POST | `/api/vendas` | JWT | Register sale |
| GET | `/api/vendas` | JWT | List sales |
| GET | `/api/vendas/periodo` | JWT | Sales by date range |
| GET | `/api/vendas/relatorio/pdf` | JWT | Download PDF report |
| GET | `/api/historico` | JWT | Full sales history |

## Project Structure

```
src/main/java/com/seuprojeto/lojadesktop/
├── config/
│   ├── SecurityConfig.java          # JWT filter chain, CORS, endpoint rules
│   └── GlobalExceptionHandler.java  # Centralized error handling
├── controller/
│   ├── AuthController.java          # Login + token validation
│   ├── ProdutoController.java
│   ├── EstoqueController.java
│   ├── ClienteController.java
│   ├── FuncionarioController.java
│   ├── VendaController.java         # Sales + PDF report
│   ├── HistoricoVendasController.java
│   └── ImageController.java
├── model/                           # JPA entities with @Valid annotations
├── repository/                      # Spring Data JPA interfaces
├── security/
│   ├── JwtUtil.java                 # Token generation and validation
│   ├── JwtRequestFilter.java        # Request interceptor
│   ├── JwtAuthenticationEntryPoint.java
│   └── LoginAttemptService.java     # Brute-force protection
├── service/                         # Business logic layer
└── util/
    ├── CryptoUtil.java              # AES encrypt/decrypt (key from env)
    └── HashUtil.java                # BCrypt wrapper
```

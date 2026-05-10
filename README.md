# Store Microservices Backend

This repository contains a Spring Boot based backend for an online store. The project is split into independent services for authentication, product catalog, payments, email notifications, service discovery, and API gateway routing.

## Services

| Service | Build tool | Default port | Responsibility |
| --- | --- | ---: | --- |
| `ApiGateway` | Maven | `8080` | Routes public traffic to backend services and validates JWTs |
| `UserService` | Gradle | `8082` | Signup, login, OAuth/JWT handling, refresh tokens, roles, JWK endpoint |
| `ProductCatalogService` | Gradle | `8083` | Product APIs, product search, category/product persistence, Redis configuration |
| `PaymentService` | Maven | `8585` | Payment link creation through Stripe, with Razorpay implementation also present |
| `EmailService` | Maven | no fixed HTTP port configured | Kafka consumer for email notifications |
| `ServiceDiscovery` | Gradle | `8761` | Eureka service registry server |

## Prerequisites

Install these before running the full system:

- Java JDK 21
- Maven 3.9+ for `ApiGateway`
- MySQL 8+
- Redis 6+ or 7+
- Apache Kafka, if you want to run `EmailService` without broker warnings
- Git

Gradle and Maven wrappers are included for most services:

- Gradle wrappers: `UserService`, `ProductCatalogService`, `ServiceDiscovery`
- Maven wrappers: `PaymentService`, `EmailService`
- `ApiGateway` currently has a `pom.xml` but no Maven wrapper, so use installed `mvn` from inside `ApiGateway`

## Local Infrastructure

### MySQL

Create the database used by `UserService` and `ProductCatalogService`:

```sql
CREATE DATABASE productcatalog;
```

The current local configs expect:

```text
host: localhost
port: 3306
database: productcatalog
username: root
password: MySql!@#123
```

For a cleaner local setup, override secrets with environment-specific configuration before sharing or deploying the project.

### Redis

`ProductCatalogService` expects Redis at:

```text
localhost:6379
```

Redis is configured through `RedisTemplate`. The service can compile and test without Redis running, but cache-related runtime flows will need it.

### Kafka

`EmailService` expects Kafka at:

```text
localhost:9092
```

The email consumer listens to topic:

```text
emailservice
```

If Kafka is not running, the service may still start during tests, but logs will show broker connection warnings.

## Environment Variables

Set these before running the services that need external integrations:

```powershell
$env:GOOGLE_CLIENT_ID="your-google-client-id"
$env:GOOGLE_CLIENT_SECRET="your-google-client-secret"
$env:STRIPE_SECRET_KEY="your-stripe-secret-key"
$env:USER_SERVICE_JWK_SET_URI="http://localhost:8082/.well-known/jwks.json"
```

Optional gateway route overrides:

```powershell
$env:USER_SERVICE_URI="http://localhost:8082"
$env:PRODUCT_SERVICE_URI="http://localhost:8083"
$env:PAYMENT_SERVICE_URI="http://localhost:8585"
```

If these variables are not set, the services use the local defaults defined in their `application.yaml` or `application.properties` files.

## Recommended Startup Order

Start infrastructure first:

1. MySQL
2. Redis
3. Kafka

Then start services:

1. `ServiceDiscovery`
2. `UserService`
3. `ProductCatalogService`
4. `PaymentService`
5. `EmailService`
6. `ApiGateway`

`ApiGateway` should be started after `UserService` because it validates JWTs using the JWK endpoint exposed by `UserService`.

## Running Each Service

Run commands from the repository root unless stated otherwise.

### ServiceDiscovery

```powershell
cd ServiceDiscovery
.\gradlew.bat bootRun
```

Health check:

```text
http://localhost:8761
```

### UserService

```powershell
cd UserService
.\gradlew.bat bootRun
```

Useful endpoints:

```text
GET  http://localhost:8082/health
GET  http://localhost:8082/status
GET  http://localhost:8082/.well-known/jwks.json
POST http://localhost:8082/auth/signup
POST http://localhost:8082/auth/login
POST http://localhost:8082/auth/refresh
POST http://localhost:8082/auth/logout
GET  http://localhost:8082/me
```

Example signup request:

```json
{
  "email": "customer@example.com",
  "name": "Customer",
  "password": "password123"
}
```

Example login request:

```json
{
  "email": "customer@example.com",
  "password": "password123"
}
```

### ProductCatalogService

```powershell
cd ProductCatalogService
.\gradlew.bat bootRun
```

Useful endpoints:

```text
GET  http://localhost:8083/products
GET  http://localhost:8083/products/{id}
POST http://localhost:8083/products/create
POST http://localhost:8083/search
```

Most product endpoints require a valid bearer token with `USER` or `ADMIN` role. Product creation requires `ADMIN`.

Example product creation request:

```json
{
  "title": "Mechanical Keyboard",
  "description": "Compact keyboard with hot-swappable switches",
  "amount": 5999,
  "imageUrl": "https://example.com/keyboard.png",
  "category": {
    "name": "Accessories"
  }
}
```

### PaymentService

```powershell
cd PaymentService
.\mvnw.cmd spring-boot:run
```

Useful endpoints:

```text
POST http://localhost:8585/payment
POST http://localhost:8585/webhook
```

Example payment request:

```json
{
  "amount": 1000,
  "orderId": "ORDER-1001",
  "name": "Customer",
  "phone": "9999999999"
}
```

`/payment` requires a valid bearer token with `USER` or `ADMIN` role.

### EmailService

```powershell
cd EmailService
.\mvnw.cmd spring-boot:run
```

This service consumes Kafka messages from the `emailservice` topic. A typical message body should match the email DTO shape used by the service:

```json
{
  "from": "sender@example.com",
  "to": "receiver@example.com",
  "subject": "Welcome",
  "body": "Thanks for signing up."
}
```

### ApiGateway

If Maven is installed:

```powershell
cd ApiGateway
mvn spring-boot:run
```

If Maven is not installed, add a Maven wrapper to `ApiGateway` or temporarily run it with an existing wrapper from a sibling Maven service:

```powershell
cd ApiGateway
..\PaymentService\mvnw.cmd spring-boot:run
```

Gateway routes:

```text
http://localhost:8080/auth/**              -> UserService
http://localhost:8080/.well-known/**       -> UserService
http://localhost:8080/me                   -> UserService
http://localhost:8080/health               -> UserService
http://localhost:8080/status               -> UserService
http://localhost:8080/products/**          -> ProductCatalogService
http://localhost:8080/search/**            -> ProductCatalogService
http://localhost:8080/payment/**           -> PaymentService
http://localhost:8080/webhook/**           -> PaymentService
```

## Running Tests

Run each service test suite independently.

```powershell
cd UserService
.\gradlew.bat test --no-daemon --console=plain
```

```powershell
cd ProductCatalogService
.\gradlew.bat test --no-daemon --console=plain
```

```powershell
cd ServiceDiscovery
.\gradlew.bat test --no-daemon --console=plain
```

```powershell
cd PaymentService
.\mvnw.cmd test
```

```powershell
cd EmailService
.\mvnw.cmd test
```

```powershell
cd ApiGateway
mvn test
```

If `mvn` is unavailable for `ApiGateway`, use:

```powershell
cd ApiGateway
..\PaymentService\mvnw.cmd test
```

## Authentication Flow

1. Register a user with `POST /auth/signup`.
2. Log in with `POST /auth/login`.
3. Copy the returned `accessToken`.
4. Call protected APIs with:

```text
Authorization: Bearer <accessToken>
```

5. Use `POST /auth/refresh` with the refresh token when the access token expires.
6. Use `POST /auth/logout` to revoke the refresh token.

## Development Notes

- `UserService` publishes JWKs at `/.well-known/jwks.json`.
- `ApiGateway` validates JWTs and forwards identity headers:
  - `X-User-Id`
  - `X-User-Email`
  - `X-User-Roles`
- `ProductCatalogService` and `PaymentService` use method-level role checks through `@PreAuthorize`.
- `PaymentService` currently selects Stripe through `PaymentGatewayStrategy`.
- `EmailService` is asynchronous and depends on Kafka for runtime message consumption.

## Current Known Gaps

- `ApiGateway` should get its own Maven wrapper for easier setup.
- Local database credentials should be moved out of YAML before deployment.
- `ProductCatalogService` has Redis configuration, but cache usage should be completed around high-read product APIs.
- `EmailService` should use test-specific Kafka configuration to avoid broker warnings during local tests.
- Payment webhooks should validate provider signatures before processing real events.
- A root-level script or compose file would make full-system startup easier.


# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

**Backend (Maven / Java 17 / Spring Boot 3.2.5):**
```bash
mvn clean package          # Build JAR
mvn spring-boot:run        # Run with default (dev) profile
mvn test                   # Run tests
```

**Run with explicit profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Frontend (React 19 + Vite, in `frontend/` directory):**
```bash
npm run dev      # Start dev server
npm run build    # Production build
npm run lint     # ESLint
npm run preview  # Preview production build
```

## Architecture

Layered Spring Boot REST API with an in-memory H2 database (dev) or PostgreSQL (prod). A React frontend lives in `frontend/`.

**Base package:** `org.example`  
**Entry point:** `src/main/java/org/example/Main.java`

### Layers

```
Controller → Service → Repository (JPA) → H2/PostgreSQL
```

- **`org.example.controller`** — REST controllers; all responses go through DTOs, never raw entities
- **`org.example.service`** — Business logic; all public methods are logged (entry `→` / exit `←`)
- **`org.example.repository`** — JPA repositories extending `JpaRepository<T, Long>`
- **`org.example.model`** — JPA entities (`Student`) and plain POJOs (`Product`, `Order`)
- **`org.example.dto`** — Request/response DTOs that decouple the API contract from entity schema
- **`org.example.exception`** — `GlobalExceptionHandler` (@RestControllerAdvice) plus domain exceptions (`StudentNotFoundException` → 404, `DuplicateEmailException` → 409)
- **`org.example.config`** — `DevDataInitializer` (seeds dev DB on startup, dev profile only), `StudentDbHealthIndicator`

### Feature Domains

| Domain | Persistence | Base Path |
|---|---|---|
| Students | JPA / H2 (dev), PostgreSQL (prod) | `/api/students` |
| Products | In-memory `ArrayList` (no DB) | `/api/products` |
| Cart / Checkout | Stateless calculation | `/api/cart/checkout` |
| Orders | In-memory list | `/api/orders` |

**Student** is the only JPA-persisted entity. Products, Orders, and Cart are in-memory.

### Profile-Driven Configuration

| Feature | Dev (`application-dev.properties`) | Prod (`application-prod.properties`) |
|---|---|---|
| DB | H2 in-memory (`jdbc:h2:mem:learnerdb`) | PostgreSQL via `DB_USERNAME` / `DB_PASSWORD` env vars |
| DDL | `create-drop` | `validate` |
| H2 Console | `/h2-console` enabled | — |
| Actuator | All endpoints exposed | `health`, `info` only |
| Log level | DEBUG for `org.example` | WARN |

### Key Patterns

- **DTO pattern enforced** — controllers accept `*RequestDTO`, return `*ResponseDTO`; never expose JPA entities directly
- **Validation** — `@Valid` on controller parameters, constraints on DTO fields (`@NotBlank`, `@Email`, `@Size`)
- **Transactional bulk ops** — `StudentService.bulkEnroll()` is `@Transactional`; one failure rolls back all
- **Pagination** — `GET /api/students/paged` uses Spring Data `Pageable` (`page`, `size`, `sortBy`, `direction`)
- **Logging convention** — Slf4j via Lombok `@Slf4j`; every service method logs `→ entering` on entry and `← returning` on exit with relevant parameters
- **Constructor injection** — no `@Autowired` field injection anywhere

## Testing

No tests exist yet. When adding tests, use `src/test/java/org/example/` mirroring the main package structure. Run with `mvn test`.

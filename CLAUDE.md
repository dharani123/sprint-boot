# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

**QuickMart** — a learning project: a Blinkit/Zepto-style 10-minute grocery store. A layered Spring Boot REST API (Java 17, Spring Boot 3.2.5) with JWT auth, Flyway-managed schema, and a React 19 + Vite frontend in `frontend/`. The code is heavily commented for teaching purposes.

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

```
Controller → Service → Repository (JPA) → H2 (dev) / PostgreSQL (prod)
```

**Base package:** `org.example`
**Entry point:** `src/main/java/org/example/Main.java`

### Layers

- **`org.example.controller`** — REST controllers; responses go through DTOs or domain entities (never raw user passwords)
- **`org.example.service`** — Business logic; all public methods are logged (entry `→` / exit `←`)
- **`org.example.repository`** — JPA repositories extending `JpaRepository<T, Long>`
- **`org.example.model`** — JPA entities: `User`, `Product`, `Order`, `OrderItem`
- **`org.example.dto`** — Request/response DTOs (`RegisterRequestDTO`, `LoginRequestDTO`, `AuthResponseDTO`, `CartItemRequest`, `CheckoutResponse`)
- **`org.example.config`** — Security (`SecurityConfig`, `JwtFilter`, `JwtUtil`), `DevDataInitializer` (seeds dev DB, dev profile only)
- **`org.example.exception`** — `GlobalExceptionHandler` (@RestControllerAdvice) plus domain exceptions (`DuplicateEmailException` → 409, `DuplicateMobileException` → 409, `InvalidCredentialsException` → 401)

### Feature Domains

| Domain | Endpoints | Auth | Persistence |
|---|---|---|---|
| Auth | `POST /api/auth/register`, `POST /api/auth/login` | public | `users` table |
| Products | `GET/POST/PUT/DELETE /api/products` | public | `products` table |
| Cart / Checkout | `POST /api/cart/checkout` | **JWT required** | writes `orders` + `order_items` |
| Orders | `GET /api/orders`, `GET /api/orders/summary` | **JWT required** | `orders` + `order_items` |
| Misc | `GET /hello`, `/greet/{name}`, `/welcome`, `GET /api/info` | public | — |

All four entities (`User`, `Product`, `Order`, `OrderItem`) are JPA-persisted. There are no in-memory collections.

### Authentication (end-to-end)

1. **Register/Login** (`AuthService`) — passwords are BCrypt-hashed; a JWT is issued (subject = email, HMAC-SHA256, 24h expiry). Login accepts **email *or* mobile** via a single `identifier` field.
2. **`JwtFilter`** (`OncePerRequestFilter`) reads `Authorization: Bearer <token>`, validates it via `JwtUtil`, and places the user's email into Spring's `SecurityContext`.
3. **`SecurityConfig`** — stateless (no server sessions), CSRF disabled (JWT in headers, not cookies), CORS open to `http://localhost:*`. Public: `/api/auth/**`, `/h2-console/**`, products, misc. Authenticated: `/api/cart/**`, `/api/orders/**`.
4. Cart/Order controllers read the logged-in email from `SecurityContextHolder.getContext().getAuthentication().getPrincipal()` to scope orders per user.

### Checkout logic (`OrderService.checkout`, `@Transactional`)

Looks up each product by ID; if any is missing, returns empty (→ 400). Computes line totals, then **delivery fee ₹25** (free when subtotal ≥ ₹199) **+ ₹3 platform fee** = total. Persists one `Order` with cascading `OrderItem`s (`cascade = ALL`, `orphanRemoval = true`). `@ManyToOne` relationships use `@JsonIgnore` to prevent serialization recursion.

### Database & Migrations

- **Flyway owns the schema** — `spring.jpa.hibernate.ddl-auto=none` in both profiles; Hibernate never alters tables.
- Migrations live in `src/main/resources/db/migration/` and run once each, in order:
  - `V1` users, `V2` products, `V3` orders + order_items, `V4` adds `user_id` FK to orders.
- Adding a schema change = add a new `V{n}__description.sql` file (never edit an applied migration).
- **Dev seeds 25 grocery products** via `DevDataInitializer` (dev profile only, skips if products already exist).

### Profile-Driven Configuration

| Feature | Dev (`application-dev.properties`) | Prod (`application-prod.properties`) |
|---|---|---|
| DB | H2 file-based (`jdbc:h2:file:./data/learnerdb`) | PostgreSQL via `DB_USERNAME` / `DB_PASSWORD` env vars |
| DDL | `none` (Flyway owns it) | `none` (Flyway owns it) |
| H2 Console | `/h2-console` enabled | disabled |
| Actuator | all endpoints exposed | `health`, `info` only |
| Log level | DEBUG for `org.example` | WARN |
| JWT secret | hardcoded dev key | `${JWT_SECRET}` env var |
| Error responses | full stacktrace + message | hidden |

### Frontend (`frontend/`)

- **`AuthContext`** stores `user` + JWT token in `localStorage` (survives refresh); exposes `login`/`logout` via `useAuth()`.
- **`ProtectedRoute`** (in `App.jsx`) redirects to `/login` when not authenticated. Routes: `/` (Store, protected), `/login`, `/register`.
- **`api.js`** `authFetch()` wraps `fetch` and auto-attaches the `Bearer` token.
- **Store page** (`pages/Store/`) — product grid with category filter, cart drawer, checkout → order confirmation modal.

### Key Patterns

- **JWT auth** — stateless; email travels in the token subject and the `SecurityContext` principal
- **Validation** — `@Valid` on controller request bodies, constraints on DTO fields (`@NotBlank`, `@Email`, `@Pattern`, `@Size`)
- **Transactional checkout** — `OrderService.checkout()` is `@Transactional`; a missing product aborts the whole order
- **Logging convention** — Slf4j via Lombok `@Slf4j`; every service method logs `→ entering` on entry and `← returning` on exit
- **Constructor injection** — via Lombok `@RequiredArgsConstructor`; no `@Autowired` field injection
- **JSON recursion guard** — `@JsonIgnore` on the back-references of JPA relationships

## Testing

No tests exist yet. When adding tests, use `src/test/java/org/example/` mirroring the main package structure. Run with `mvn test`.

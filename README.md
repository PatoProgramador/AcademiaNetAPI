# AcademiaNet — Backend (API REST)

Backend del sistema de gestión académica **multi-tenant** AcademiaNet, construido con
**Spring Boot 4 / Java 21 / PostgreSQL**.

> Arquitectura multi-tenant: **todas** las tablas tienen `company_id` (antes
> `empresa_id`) para aislar los datos entre organizaciones. Flujo académico:
> `subjects → courses → enrollments → grades`.

---

## Stack

| Capa | Tecnología |
|------|------------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.6 (Web MVC + Data JPA) |
| ORM | Hibernate 7 (soft-delete con `@SQLDelete` / `@SQLRestriction`) |
| Base de datos | PostgreSQL 16 (H2 en memoria para tests) |
| Auth | JWT (Bearer) con Spring Security — stateless |
| Build | Maven (wrapper incluido) |
| Contenedores | Dockerfile multi-stage + docker-compose |

---

## Cómo ejecutar

### Opción A — Docker Compose (recomendada)

```bash
docker compose up --build
```

Levanta PostgreSQL + la API en `http://localhost:8080`. El seeder carga datos demo
en el primer arranque.

### Opción B — Local con Maven

Requiere un PostgreSQL accesible. Variables de entorno (con sus valores por defecto):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/academianet
export DB_USERNAME=academianet
export DB_PASSWORD=academianet
./mvnw spring-boot:run
```

### Tests

```bash
./mvnw test     # usa H2 en memoria, no requiere Postgres
```

---

## Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/academianet` | URL JDBC |
| `DB_USERNAME` / `DB_PASSWORD` | `academianet` | Credenciales BD |
| `JPA_DDL_AUTO` | `update` | Estrategia DDL de Hibernate |
| `SEED_ENABLED` | `true` | Carga datos demo al arrancar (se omite si ya hay datos) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Orígenes del front |
| `JWT_SECRET` | `academianet-dev-secret-…` | Clave de firma HMAC (mín. 32 bytes). **Cambiar en producción** |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Vigencia del token en ms |
| `SERVER_PORT` | `8080` | Puerto HTTP |

---

## Credenciales de prueba

Contraseña para todos: **`123456`** (igual que el front).

| Rol (front) | Email | Código interno |
|-------------|-------|----------------|
| `admin` | `admin@test.com` | `ADMINISTRATOR` |
| `profesor` | `profesor@test.com` | `PROFESSOR` |
| `estudiante` | `estudiante@test.com` | `STUDENT` |

---

## Autenticación (JWT)

1. `POST /api/auth/login` con `{email, password}` → devuelve `{ token, tokenType: "Bearer", expiresInMs, ... }`.
2. En cada petición a un endpoint protegido envía el header:

   ```
   Authorization: Bearer <token>
   ```

Rutas **públicas** (sin token): `/api/auth/login`, `/v3/api-docs/**`, `/swagger-ui/**`.
Todo lo demás bajo `/api/**` exige un token válido (responde `401` si falta o es inválido).
El token incluye `sub` (userId), `email`, `role`, `companyId`, `name` y `exp`. Es **stateless**:
no hay sesión en el servidor.

En Swagger UI usa el botón **Authorize** y pega el token para probar los endpoints protegidos.

## Documentación interactiva (Swagger / OpenAPI)

Con la app corriendo:

| Recurso | URL |
|---------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON (3.1) | http://localhost:8080/v3/api-docs |

Desde Swagger UI el front puede ver todos los endpoints, sus esquemas de
request/response y probarlos con **"Try it out"** (mismo origen, sin problemas de CORS).
Generado con springdoc-openapi 3.0.3.

## Endpoints

Base: `/api`. Todos exigen `Authorization: Bearer <token>` salvo el login.
El `companyId` es opcional; si se omite se usa la empresa demo.

| Método | Ruta | Descripción | Panel |
|--------|------|-------------|-------|
| `POST` | `/api/auth/login` | Login (público). Devuelve `{token,tokenType,expiresInMs,id,name,email,role,companyId,companyName}` | Login |
| `GET` | `/api/users` | Lista usuarios | Admin |
| `POST` | `/api/users` | Crea usuario `{name,email,role,password?}` | Admin |
| `PUT` | `/api/users/{id}` | Edita usuario | Admin |
| `DELETE` | `/api/users/{id}` | Elimina (soft-delete) | Admin |
| `GET` | `/api/courses` | Lista cursos con promedio calculado | Estudiante |
| `GET` | `/api/students` | Lista alumnos (`?professorId=` para los de un profesor) | Profesor |
| `GET` | `/api/students/{id}/grades` | Notas publicadas de un estudiante | Estudiante |
| `PUT` | `/api/grades/{id}` | Actualiza una nota `{value,published?}` | Profesor |
| `GET` | `/api/dashboard/stats` | Métricas globales | Admin |

Ejemplo:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"estudiante@test.com","password":"123456"}'
```

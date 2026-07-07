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
| Datos auxiliares de usuario | MongoDB 7 (preferencias + log de actividad) |
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
./mvnw test     # usa H2 en memoria, no requiere Postgres ni Mongo
```

---

## Persistencia políglota: PostgreSQL + MongoDB

El modelo académico (usuarios, cursos, notas, matrículas…) es **relacional** y vive en
PostgreSQL: el `User` con sus claves foráneas hacia `Enrollment`, `Grade`, `Course` y
`AcademicRecord` sigue siendo la fuente de verdad de la identidad.

MongoDB se añade **solo para datos auxiliares y flexibles de usuario**, asociados por
`userId` (el UUID de Postgres, sin duplicar la identidad):

| Colección | Contenido |
|-----------|-----------|
| `user_preferences` | Preferencias de UI: `theme`, `language`, `emailNotifications`, `timezone` |
| `user_activity` | Log de eventos: `LOGIN`, `PREFERENCES_UPDATE`, … con `timestamp` |

La conexión a Mongo es **perezosa y tolerante a fallos**: si Mongo no está disponible la API
arranca igual, el login funciona y el registro de actividad simplemente se omite (queda un
warning). Los endpoints de preferencias/actividad sí requieren Mongo.

- **Local (Docker Compose):** el `docker-compose.yml` levanta un servicio `mongo:7` y le pasa
  a la API `MONGODB_URI=mongodb://mongo:27017/academianet`.
- **Render:** Render no ofrece Mongo gestionado. Crea un cluster gratuito en
  [MongoDB Atlas](https://www.mongodb.com/atlas) y define `MONGODB_URI` en el dashboard del
  servicio (el `render.yaml` ya declara la variable con `sync: false`).

---

## Despliegue en Render

El repo incluye un blueprint [`render.yaml`](render.yaml) que crea **el Postgres + el web service** (Docker) automáticamente.

1. Sube el repo a GitHub (rama `main`).
2. En Render: **New → Blueprint**, conecta el repo y elige la rama. Render lee `render.yaml`, crea la base de datos `academianet-db` y el servicio `academianet-api`, e inyecta solas las credenciales de BD y un `JWT_SECRET` aleatorio.
3. **Apply** y espera el build (compila el Dockerfile multi-stage). La API queda en `https://academianet-api.onrender.com`.
4. (Opcional) Ajusta `CORS_ALLOWED_ORIGINS` con la URL del front desplegado.

Verifica con `POST https://<tu-servicio>.onrender.com/api/auth/login` y abre `https://<tu-servicio>.onrender.com/swagger-ui.html`.

> Plan free: la BD expira a los 30 días y el servicio se duerme tras inactividad (el primer request lo despierta). El puerto lo gestiona Render con `PORT` (la app ya lo respeta).

---

## Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_URL` | _(compuesta)_ | URL JDBC completa; si se define tiene prioridad sobre `DB_HOST/PORT/NAME` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `5432` / `academianet` | Componentes de la BD (los usa Render vía `fromDatabase`) |
| `DB_USERNAME` / `DB_PASSWORD` | `academianet` | Credenciales BD |
| `MONGODB_URI` | `mongodb://localhost:27017/academianet` | Conexión a MongoDB (datos auxiliares de usuario). En Render: cadena de MongoDB Atlas |
| `JPA_DDL_AUTO` | `update` | Estrategia DDL de Hibernate |
| `SEED_ENABLED` | `true` | Carga datos demo al arrancar (se omite si ya hay datos) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Orígenes del front |
| `JWT_SECRET` | `academianet-dev-secret-…` | Semilla de la clave HMAC (se deriva a 32 bytes con SHA-256). **Cambiar en producción** |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Vigencia del token en ms |
| `PORT` / `SERVER_PORT` | `8080` | Puerto HTTP (`PORT` lo fija Render) |

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
| `GET` | `/api/users/{id}/preferences` | Preferencias del usuario (MongoDB) | Perfil |
| `PUT` | `/api/users/{id}/preferences` | Actualiza preferencias `{theme,language,emailNotifications,timezone}` | Perfil |
| `GET` | `/api/users/{id}/activity` | Log de actividad del usuario (MongoDB) | Perfil |
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

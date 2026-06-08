# AcademiaNet — Backend (API REST)

Backend del sistema de gestión académica **multi-tenant** AcademiaNet, construido con
**Spring Boot 4 / Java 21 / PostgreSQL**. Implementa las 15 tablas del MER
(`Multitenant.html`) con **nombres en inglés** y expone una API compatible con el
front React (`App.tsx`).

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
| Auth | Login simple sin JWT (sesión del lado del cliente) |
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

Base: `/api`. El `companyId` es opcional; si se omite se usa la empresa demo.

| Método | Ruta | Descripción | Panel |
|--------|------|-------------|-------|
| `POST` | `/api/auth/login` | Login. Devuelve `{id,name,email,role,companyId,companyName}` con `role` ya en formato del front | Login |
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

---

## Mapeo de tablas y columnas (Español → Inglés)

| MER (español) | Entidad / tabla (inglés) |
|---------------|--------------------------|
| EMPRESAS | `Company` / `companies` |
| ROLES | `Role` / `roles` |
| PERMISOS | `Permission` / `permissions` |
| ROLES_PERMISOS | `RolePermission` / `role_permissions` |
| USUARIOS | `User` / `users` |
| PROGRAMAS | `Program` / `programs` |
| MATERIAS | `Subject` / `subjects` |
| PRERREQUISITOS | `Prerequisite` / `prerequisites` |
| PERIODOS_ACADEMICOS | `AcademicPeriod` / `academic_periods` |
| AULAS | `Classroom` / `classrooms` |
| CURSOS | `Course` / `courses` |
| EVALUACIONES | `Evaluation` / `evaluations` |
| MATRICULAS_CURSOS | `Enrollment` / `enrollments` |
| NOTAS | `Grade` / `grades` |
| HISTORIAL_ACADEMICO | `AcademicRecord` / `academic_records` |

Columnas: `empresa_id→company_id`, `nombre→name/first_name`, `apellido→last_name`,
`correo→email`, `documento_*→document_*`, `creditos→credits`, `cupo_maximo→max_capacity`,
`valor→grade_value`, `publicada→published`, `nota_final→final_grade`, etc.
Auditoría universal: `created_at`, `updated_at`, `deleted_at` (soft-delete).

---

## Notas sobre la compatibilidad con el front

- El front (`App.tsx`) hoy usa datos mock; para conectarlo basta reemplazar esos
  arreglos por `fetch` a los endpoints anteriores.
- El campo `role` de `/api/auth/login` ya viene como `"estudiante" | "profesor" | "admin"`,
  exactamente los valores del tipo `Role` del front.
- Las notas se sembraron con `max_value = 10.00` para que coincidan con la escala 0–10
  que muestra el front (el MER define `5.00` como *default*, no como límite).
- `attendance_percentage` en `enrollments` es una extensión pragmática del MER para
  alimentar la columna de asistencia del panel del profesor.

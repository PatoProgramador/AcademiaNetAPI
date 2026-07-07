# Diagrama entidad-relación — AcademiaNet

Sistema académico **multi-tenant**. La tabla `companies` es la raíz del tenant;
todas las demás tablas la referencian mediante `company_id`.

Toda entidad hereda de `BaseEntity`, por lo que además de las columnas mostradas,
cada tabla tiene: `id` (UUID, PK), `created_at`, `updated_at` y `deleted_at`.
El campo `deleted_at` implementa **soft-delete** (`@SQLDelete` + `@SQLRestriction`).

Notación: `||--o{` = uno-a-muchos (crow's foot) · `PK` primaria · `FK` foránea · `UK` única.

```mermaid
erDiagram
  COMPANY ||--o{ ROLE : "define"
  COMPANY ||--o{ PERMISSION : "define"
  COMPANY ||--o{ USER : "tiene"
  COMPANY ||--o{ PROGRAM : "ofrece"
  COMPANY ||--o{ SUBJECT : "gestiona"
  COMPANY ||--o{ ACADEMIC_PERIOD : "gestiona"
  COMPANY ||--o{ CLASSROOM : "posee"
  COMPANY ||--o{ COURSE : "gestiona"
  COMPANY ||--o{ ENROLLMENT : "gestiona"
  COMPANY ||--o{ EVALUATION : "gestiona"
  COMPANY ||--o{ GRADE : "gestiona"
  COMPANY ||--o{ ACADEMIC_RECORD : "gestiona"
  COMPANY ||--o{ ROLE_PERMISSION : "gestiona"
  COMPANY ||--o{ PREREQUISITE : "gestiona"

  ROLE ||--o{ USER : "asignado a"
  ROLE ||--o{ ROLE_PERMISSION : "agrupa"
  PERMISSION ||--o{ ROLE_PERMISSION : "otorgado en"

  PROGRAM ||--o{ SUBJECT : "contiene"
  SUBJECT ||--o{ PREREQUISITE : "requiere"
  SUBJECT ||--o{ PREREQUISITE : "es prerreq de"
  SUBJECT ||--o{ COURSE : "se dicta como"
  SUBJECT ||--o{ ACADEMIC_RECORD : "historial de"

  ACADEMIC_PERIOD ||--o{ COURSE : "ubica"
  ACADEMIC_PERIOD ||--o{ ENROLLMENT : "ubica"
  ACADEMIC_PERIOD ||--o{ ACADEMIC_RECORD : "ubica"
  CLASSROOM ||--o{ COURSE : "aloja"

  USER ||--o{ COURSE : "dicta (profesor)"
  USER ||--o{ ENROLLMENT : "cursa (estudiante)"
  USER ||--o{ ACADEMIC_RECORD : "acumula (estudiante)"
  USER ||--o{ GRADE : "registra (profesor)"

  COURSE ||--o{ ENROLLMENT : "recibe"
  COURSE ||--o{ EVALUATION : "define"
  ENROLLMENT ||--o{ GRADE : "obtiene"
  EVALUATION ||--o{ GRADE : "califica"

  COMPANY {
    uuid id PK
    varchar name
    varchar nit UK
    varchar domain UK
    enum subscription_plan
    bool active
  }
  ROLE {
    uuid id PK
    uuid company_id FK
    varchar name
    varchar code
  }
  PERMISSION {
    uuid id PK
    uuid company_id FK
    varchar resource
    enum action
  }
  ROLE_PERMISSION {
    uuid id PK
    uuid company_id FK
    uuid role_id FK
    uuid permission_id FK
  }
  USER {
    uuid id PK
    uuid company_id FK
    uuid role_id FK
    varchar first_name
    varchar last_name
    varchar email UK
    varchar document_number
    bool active
  }
  PROGRAM {
    uuid id PK
    uuid company_id FK
    varchar name
    varchar code
    enum level
    int duration_semesters
  }
  SUBJECT {
    uuid id PK
    uuid company_id FK
    uuid program_id FK
    varchar name
    varchar code
    int credits
    bool mandatory
  }
  PREREQUISITE {
    uuid id PK
    uuid company_id FK
    uuid subject_id FK
    uuid prerequisite_id FK
  }
  ACADEMIC_PERIOD {
    uuid id PK
    uuid company_id FK
    varchar name
    varchar code
    date start_date
    date end_date
    enum status
  }
  CLASSROOM {
    uuid id PK
    uuid company_id FK
    varchar name
    varchar code
    int capacity
    enum type
  }
  COURSE {
    uuid id PK
    uuid company_id FK
    uuid subject_id FK
    uuid professor_id FK
    uuid period_id FK
    uuid classroom_id FK
    varchar name
    int max_capacity
    enum modality
    enum status
  }
  ENROLLMENT {
    uuid id PK
    uuid company_id FK
    uuid student_id FK
    uuid course_id FK
    uuid period_id FK
    date enrollment_date
    enum status
    enum enrollment_type
  }
  EVALUATION {
    uuid id PK
    uuid company_id FK
    uuid course_id FK
    varchar name
    enum type
    numeric percentage
    date date
  }
  GRADE {
    uuid id PK
    uuid company_id FK
    uuid enrollment_id FK
    uuid evaluation_id FK
    uuid professor_id FK
    numeric grade_value
    numeric max_value
    bool published
  }
  ACADEMIC_RECORD {
    uuid id PK
    uuid company_id FK
    uuid student_id FK
    uuid subject_id FK
    uuid period_id FK
    numeric final_grade
    enum status
    int credits_earned
  }
```

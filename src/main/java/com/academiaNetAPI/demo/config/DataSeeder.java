package com.academiaNetAPI.demo.config;

import com.academiaNetAPI.demo.common.PasswordHasher;
import com.academiaNetAPI.demo.common.RoleCodes;
import com.academiaNetAPI.demo.entity.AcademicPeriod;
import com.academiaNetAPI.demo.entity.AcademicRecord;
import com.academiaNetAPI.demo.entity.Classroom;
import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Course;
import com.academiaNetAPI.demo.entity.Enrollment;
import com.academiaNetAPI.demo.entity.Evaluation;
import com.academiaNetAPI.demo.entity.Grade;
import com.academiaNetAPI.demo.entity.Permission;
import com.academiaNetAPI.demo.entity.Prerequisite;
import com.academiaNetAPI.demo.entity.Program;
import com.academiaNetAPI.demo.entity.Role;
import com.academiaNetAPI.demo.entity.RolePermission;
import com.academiaNetAPI.demo.entity.Subject;
import com.academiaNetAPI.demo.entity.User;
import com.academiaNetAPI.demo.enums.ClassroomType;
import com.academiaNetAPI.demo.enums.CourseModality;
import com.academiaNetAPI.demo.enums.CourseStatus;
import com.academiaNetAPI.demo.enums.EnrollmentStatus;
import com.academiaNetAPI.demo.enums.EnrollmentType;
import com.academiaNetAPI.demo.enums.EvaluationType;
import com.academiaNetAPI.demo.enums.PeriodStatus;
import com.academiaNetAPI.demo.enums.PermissionAction;
import com.academiaNetAPI.demo.enums.ProgramLevel;
import com.academiaNetAPI.demo.enums.RecordStatus;
import com.academiaNetAPI.demo.enums.SubscriptionPlan;
import com.academiaNetAPI.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
@ConditionalOnProperty(name = "academianet.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String PWD = PasswordHasher.hash("123456");
    private static final BigDecimal MAX = new BigDecimal("10.00");

    private final CompanyRepository companyRepo;
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermissionRepo;
    private final UserRepository userRepo;
    private final ProgramRepository programRepo;
    private final SubjectRepository subjectRepo;
    private final PrerequisiteRepository prerequisiteRepo;
    private final AcademicPeriodRepository periodRepo;
    private final ClassroomRepository classroomRepo;
    private final CourseRepository courseRepo;
    private final EvaluationRepository evaluationRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final GradeRepository gradeRepo;
    private final AcademicRecordRepository academicRecordRepo;

    public DataSeeder(CompanyRepository companyRepo, RoleRepository roleRepo, PermissionRepository permissionRepo,
                      RolePermissionRepository rolePermissionRepo, UserRepository userRepo, ProgramRepository programRepo,
                      SubjectRepository subjectRepo, PrerequisiteRepository prerequisiteRepo,
                      AcademicPeriodRepository periodRepo, ClassroomRepository classroomRepo,
                      CourseRepository courseRepo, EvaluationRepository evaluationRepo,
                      EnrollmentRepository enrollmentRepo, GradeRepository gradeRepo,
                      AcademicRecordRepository academicRecordRepo) {
        this.companyRepo = companyRepo;
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.rolePermissionRepo = rolePermissionRepo;
        this.userRepo = userRepo;
        this.programRepo = programRepo;
        this.subjectRepo = subjectRepo;
        this.prerequisiteRepo = prerequisiteRepo;
        this.periodRepo = periodRepo;
        this.classroomRepo = classroomRepo;
        this.courseRepo = courseRepo;
        this.evaluationRepo = evaluationRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.gradeRepo = gradeRepo;
        this.academicRecordRepo = academicRecordRepo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (companyRepo.count() > 0) {
            log.info("Seed omitido: ya existen datos.");
            return;
        }
        log.info("Sembrando datos demo de AcademiaNet...");

        Company company = new Company();
        company.setName("AcademiaNet Demo University");
        company.setNit("900123456-7");
        company.setDomain("demo.academianet.edu");
        company.setTimezone("America/Bogota");
        company.setCurrency("COP");
        company.setCountry("CO");
        company.setSubscriptionPlan(SubscriptionPlan.ENTERPRISE);
        company.setActive(true);
        companyRepo.save(company);

        Role adminRole = role(company, "Administrador", RoleCodes.ADMINISTRATOR, "Gestión total por empresa");
        Role profRole = role(company, "Profesor", RoleCodes.PROFESSOR, "Crea cursos y registra notas");
        Role studentRole = role(company, "Estudiante", RoleCodes.STUDENT, "Se matricula y consulta notas");

        for (PermissionAction action : PermissionAction.values()) {
            Permission perm = new Permission();
            perm.setCompany(company);
            perm.setResource("GRADES");
            perm.setAction(action);
            permissionRepo.save(perm);

            RolePermission rp = new RolePermission();
            rp.setCompany(company);
            rp.setRole(adminRole);
            rp.setPermission(perm);
            rolePermissionRepo.save(rp);
        }

        Program program = new Program();
        program.setCompany(company);
        program.setName("Ingeniería de Sistemas");
        program.setCode("ING-SIS");
        program.setLevel(ProgramLevel.UNIVERSITY);
        program.setDurationSemesters(10);
        program.setActive(true);
        programRepo.save(program);

        Classroom room201 = classroom(company, "Salón 201", "A-201", "Bloque A", 40, ClassroomType.CLASSROOM);
        Classroom labB = classroom(company, "Laboratorio de Cómputo", "LAB-1", "Bloque B", 30, ClassroomType.LAB);

        AcademicPeriod period = new AcademicPeriod();
        period.setCompany(company);
        period.setName("Ciclo 2026-1");
        period.setCode("2026-1");
        period.setStartDate(LocalDate.of(2026, 1, 20));
        period.setEndDate(LocalDate.of(2026, 6, 12));
        period.setStatus(PeriodStatus.ACTIVE);
        periodRepo.save(period);

        User admin = user(company, adminRole, "Administrador", "Sistema", "admin@test.com", null, null);

        User carlos = user(company, profRole, "Dr. Carlos", "Mendoza", "profesor@test.com", "CC", "10001");
        User flores = user(company, profRole, "Dra.", "Flores", "flores@test.com", "CC", "10002");
        User vazquez = user(company, profRole, "Ing.", "Vázquez", "vazquez@test.com", "CC", "10003");
        User torres = user(company, profRole, "M.C.", "Torres", "torres@test.com", "CC", "10004");
        User sanchez = user(company, profRole, "Ing.", "Sánchez", "sanchez@test.com", "CC", "10005");
        User moreno = user(company, profRole, "Lic.", "Moreno", "moreno@test.com", "CC", "10006");

        User ana = student(company, studentRole, "Ana", "García López", "estudiante@test.com", "2023001");
        User luis = student(company, studentRole, "Luis", "Ramírez Torres", "luis.ramirez@test.com", "2023002");
        User maria = student(company, studentRole, "María", "Fernández Cruz", "maria.fernandez@test.com", "2023003");
        User carlosJ = student(company, studentRole, "Carlos", "Jiménez Vega", "carlos.jimenez@test.com", "2023004");
        User sofia = student(company, studentRole, "Sofía", "Morales Díaz", "sofia.morales@test.com", "2023005");
        User roberto = student(company, studentRole, "Roberto", "Castillo Ruiz", "roberto.castillo@test.com", "2023006");
        User valeria = student(company, studentRole, "Valeria", "Reyes Gómez", "valeria.reyes@test.com", "2023007");
        User miguel = student(company, studentRole, "Miguel Ángel", "Soto", "miguel.soto@test.com", "2023008");

        Subject calculo = subject(company, program, "Cálculo Diferencial e Integral", "MAT-101", 6);
        Subject poo = subject(company, program, "Programación Orientada a Objetos", "INF-203", 5);
        Subject algebra = subject(company, program, "Álgebra Lineal", "MAT-102", 5);
        Subject fisica = subject(company, program, "Física General", "FIS-101", 6);
        Subject estadistica = subject(company, program, "Estadística y Probabilidad", "MAT-203", 4);
        Subject bd = subject(company, program, "Bases de Datos", "INF-305", 5);
        Subject redes = subject(company, program, "Redes de Computadoras", "INF-401", 4);
        Subject etica = subject(company, program, "Ética Profesional", "HUM-101", 3);

        Prerequisite pre = new Prerequisite();
        pre.setCompany(company);
        pre.setSubject(estadistica);
        pre.setPrerequisite(calculo);
        prerequisiteRepo.save(pre);

        Course cCalculo = course(company, calculo, carlos, period, room201, "Lun-Mié-Vie 08:00");
        Course cPoo = course(company, poo, carlos, period, labB, "Mar-Jue 10:00");
        Course cAlgebra = course(company, algebra, flores, period, room201, "Lun-Mié 12:00");
        Course cFisica = course(company, fisica, carlos, period, room201, "Mar-Jue-Sáb 09:00");
        Course cEstadistica = course(company, estadistica, vazquez, period, room201, "Vie 14:00");
        Course cBd = course(company, bd, torres, period, labB, "Lun-Mié 16:00");
        course(company, redes, sanchez, period, labB, "Jue 08:00");
        course(company, etica, moreno, period, room201, "Mar 15:00");

        enrollWithAverage(company, ana, cPoo, period, 95, "8.7");
        enrollWithAverage(company, luis, cPoo, period, 88, "7.5");
        enrollWithAverage(company, maria, cPoo, period, 92, "9.2");
        enrollWithAverage(company, carlosJ, cPoo, period, 75, "6.8");
        enrollWithAverage(company, sofia, cPoo, period, 98, "9.8");
        enrollWithAverage(company, roberto, cPoo, period, 80, "7.2");
        enrollWithAverage(company, valeria, cPoo, period, 91, "8.5");
        enrollWithAverage(company, miguel, cPoo, period, 70, "6.0");

        Enrollment anaCalc = enroll(company, ana, cCalculo, period, 95);
        Enrollment anaAlg = enroll(company, ana, cAlgebra, period, 95);
        Enrollment anaFis = enroll(company, ana, cFisica, period, 95);
        Enrollment anaBd = enroll(company, ana, cBd, period, 95);

        grade(company, anaCalc, carlos, "Examen Parcial 1 – Cálculo", EvaluationType.EXAM,
                LocalDate.of(2026, 5, 15), "8.5");

        Enrollment anaPooEnr = enrollmentRepo.findByStudent(ana).stream()
                .filter(e -> e.getCourse().getId().equals(cPoo.getId())).findFirst().orElseThrow();
        grade(company, anaPooEnr, carlos, "Práctica de Laboratorio 3", EvaluationType.PRACTICE,
                LocalDate.of(2026, 5, 12), "9.0");
        grade(company, anaAlg, flores, "Tarea: Matrices y Vectores", EvaluationType.HOMEWORK,
                LocalDate.of(2026, 5, 10), "7.5");
        grade(company, anaFis, carlos, "Quiz Electromagnetismo", EvaluationType.QUIZ,
                LocalDate.of(2026, 5, 8), "8.0");
        grade(company, anaBd, torres, "Proyecto: Sistema CRUD", EvaluationType.PROJECT,
                LocalDate.of(2026, 5, 5), "9.5");

        AcademicRecord record = new AcademicRecord();
        record.setCompany(company);
        record.setStudent(ana);
        record.setSubject(calculo);
        record.setPeriod(period);
        record.setFinalGrade(new BigDecimal("8.50"));
        record.setStatus(RecordStatus.PASSED);
        record.setCreditsEarned(6);
        academicRecordRepo.save(record);

        log.info("Seed completado: 1 empresa, {} usuarios, {} cursos.",
                userRepo.count(), courseRepo.count());
    }

    private Role role(Company company, String name, String code, String desc) {
        Role r = new Role();
        r.setCompany(company);
        r.setName(name);
        r.setCode(code);
        r.setDescription(desc);
        return roleRepo.save(r);
    }

    private Classroom classroom(Company company, String name, String code, String building, int capacity, ClassroomType type) {
        Classroom c = new Classroom();
        c.setCompany(company);
        c.setName(name);
        c.setCode(code);
        c.setBuilding(building);
        c.setCapacity(capacity);
        c.setType(type);
        return classroomRepo.save(c);
    }

    private User user(Company company, Role role, String first, String last, String email, String docType, String docNumber) {
        User u = new User();
        u.setCompany(company);
        u.setRole(role);
        u.setFirstName(first);
        u.setLastName(last);
        u.setEmail(email);
        u.setPasswordHash(PWD);
        u.setDocumentType(docType);
        u.setDocumentNumber(docNumber);
        u.setActive(true);
        return userRepo.save(u);
    }

    private User student(Company company, Role role, String first, String last, String email, String matricula) {
        return user(company, role, first, last, email, "TI", matricula);
    }

    private Subject subject(Company company, Program program, String name, String code, int credits) {
        Subject s = new Subject();
        s.setCompany(company);
        s.setProgram(program);
        s.setName(name);
        s.setCode(code);
        s.setCredits(credits);
        s.setWeeklyHours(credits);
        s.setMandatory(true);
        s.setActive(true);
        return subjectRepo.save(s);
    }

    private Course course(Company company, Subject subject, User professor, AcademicPeriod period,
                          Classroom classroom, String schedule) {
        Course c = new Course();
        c.setCompany(company);
        c.setSubject(subject);
        c.setProfessor(professor);
        c.setPeriod(period);
        c.setClassroom(classroom);
        c.setName(subject.getName());
        c.setCode(subject.getCode());
        c.setSchedule(schedule);
        c.setMaxCapacity(classroom.getCapacity());
        c.setAvailableCapacity(classroom.getCapacity());
        c.setModality(classroom.getType() == ClassroomType.LAB ? CourseModality.HYBRID : CourseModality.IN_PERSON);
        c.setStatus(CourseStatus.IN_PROGRESS);
        return courseRepo.save(c);
    }

    private Enrollment enroll(Company company, User student, Course course, AcademicPeriod period, int attendance) {
        Enrollment e = new Enrollment();
        e.setCompany(company);
        e.setStudent(student);
        e.setCourse(course);
        e.setPeriod(period);
        e.setEnrollmentDate(period.getStartDate());
        e.setStatus(EnrollmentStatus.ACTIVE);
        e.setEnrollmentType(EnrollmentType.REGULAR);
        e.setAttendancePercentage(attendance);
        return enrollmentRepo.save(e);
    }

    private void enrollWithAverage(Company company, User student, Course course, AcademicPeriod period,
                                   int attendance, String average) {
        Enrollment e = enroll(company, student, course, period, attendance);
        grade(company, e, course.getProfessor(), "Promedio del curso", EvaluationType.EXAM,
                period.getStartDate(), average);
    }

    private void grade(Company company, Enrollment enrollment, User professor, String evaluationName,
                       EvaluationType type, LocalDate date, String value) {
        Evaluation evaluation = new Evaluation();
        evaluation.setCompany(company);
        evaluation.setCourse(enrollment.getCourse());
        evaluation.setName(evaluationName);
        evaluation.setType(type);
        evaluation.setPercentage(new BigDecimal("100.00"));
        evaluation.setDate(date);
        evaluationRepo.save(evaluation);

        Grade grade = new Grade();
        grade.setCompany(company);
        grade.setEnrollment(enrollment);
        grade.setEvaluation(evaluation);
        grade.setProfessor(professor);
        grade.setValue(new BigDecimal(value));
        grade.setMaxValue(MAX);
        grade.setPublished(true);
        grade.setRecordDate(OffsetDateTime.now());
        gradeRepo.save(grade);
    }
}

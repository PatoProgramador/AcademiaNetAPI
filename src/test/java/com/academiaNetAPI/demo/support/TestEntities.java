package com.academiaNetAPI.demo.support;

import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Course;
import com.academiaNetAPI.demo.entity.Enrollment;
import com.academiaNetAPI.demo.entity.Evaluation;
import com.academiaNetAPI.demo.entity.Grade;
import com.academiaNetAPI.demo.entity.Role;
import com.academiaNetAPI.demo.entity.Subject;
import com.academiaNetAPI.demo.entity.User;
import com.academiaNetAPI.demo.enums.CourseModality;
import com.academiaNetAPI.demo.enums.CourseStatus;
import com.academiaNetAPI.demo.enums.EvaluationType;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestEntities {

    private TestEntities() {}

    public static Company company(String name) {
        Company c = new Company();
        c.setId(UUID.randomUUID());
        c.setName(name);
        c.setActive(true);
        return c;
    }

    public static Role role(Company company, String code) {
        Role r = new Role();
        r.setId(UUID.randomUUID());
        r.setCompany(company);
        r.setCode(code);
        r.setName(code);
        return r;
    }

    public static User user(Company company, Role role, String first, String last, String email) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setCompany(company);
        u.setRole(role);
        u.setFirstName(first);
        u.setLastName(last);
        u.setEmail(email);
        u.setActive(true);
        return u;
    }

    public static User student(Company company, Role role, String first, String last, String document) {
        User u = user(company, role, first, last, first.toLowerCase() + "@test.com");
        u.setDocumentNumber(document);
        return u;
    }

    public static Subject subject(Company company, String name, String code, int credits) {
        Subject s = new Subject();
        s.setId(UUID.randomUUID());
        s.setCompany(company);
        s.setName(name);
        s.setCode(code);
        s.setCredits(credits);
        return s;
    }

    public static Course course(Company company, Subject subject, User professor) {
        Course c = new Course();
        c.setId(UUID.randomUUID());
        c.setCompany(company);
        c.setSubject(subject);
        c.setProfessor(professor);
        c.setName(subject.getName());
        c.setCode(subject.getCode());
        c.setSchedule("Lun 08:00");
        c.setModality(CourseModality.IN_PERSON);
        c.setStatus(CourseStatus.IN_PROGRESS);
        return c;
    }

    public static Enrollment enrollment(Company company, User studentUser, Course course, Integer attendance) {
        Enrollment e = new Enrollment();
        e.setId(UUID.randomUUID());
        e.setCompany(company);
        e.setStudent(studentUser);
        e.setCourse(course);
        e.setAttendancePercentage(attendance);
        return e;
    }

    public static Evaluation evaluation(Company company, Course course, String name, EvaluationType type) {
        Evaluation ev = new Evaluation();
        ev.setId(UUID.randomUUID());
        ev.setCompany(company);
        ev.setCourse(course);
        ev.setName(name);
        ev.setType(type);
        return ev;
    }

    public static Grade grade(Company company, Enrollment enrollment, Evaluation evaluation, String value, boolean published) {
        Grade g = new Grade();
        g.setId(UUID.randomUUID());
        g.setCompany(company);
        g.setEnrollment(enrollment);
        g.setEvaluation(evaluation);
        g.setValue(new BigDecimal(value));
        g.setMaxValue(new BigDecimal("10.00"));
        g.setPublished(published);
        return g;
    }
}

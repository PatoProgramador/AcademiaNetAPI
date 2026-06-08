package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.GradeResponse;
import com.academiaNetAPI.demo.dto.StudentResponse;
import com.academiaNetAPI.demo.service.GradeService;
import com.academiaNetAPI.demo.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final GradeService gradeService;

    public StudentController(StudentService studentService, GradeService gradeService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
    }

    /** Lista de alumnos; si se pasa professorId, solo los de sus cursos. */
    @GetMapping
    public List<StudentResponse> list(@RequestParam(required = false) UUID companyId,
                                      @RequestParam(required = false) UUID professorId) {
        if (professorId != null) {
            return studentService.listByProfessor(professorId);
        }
        return studentService.list(companyId);
    }

    /** Notas publicadas de un estudiante. */
    @GetMapping("/{id}/grades")
    public List<GradeResponse> grades(@PathVariable UUID id) {
        return gradeService.recentForStudent(id);
    }
}

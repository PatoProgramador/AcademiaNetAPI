package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.GradeResponse;
import com.academiaNetAPI.demo.dto.StudentResponse;
import com.academiaNetAPI.demo.service.GradeService;
import com.academiaNetAPI.demo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Estudiantes", description = "Alumnos y sus notas")
public class StudentController {

    private final StudentService studentService;
    private final GradeService gradeService;

    public StudentController(StudentService studentService, GradeService gradeService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
    }

    @GetMapping
    @Operation(summary = "Listar alumnos",
            description = "Si se pasa professorId, devuelve solo los alumnos de sus cursos.")
    public List<StudentResponse> list(@RequestParam(required = false) UUID companyId,
                                      @RequestParam(required = false) UUID professorId) {
        if (professorId != null) {
            return studentService.listByProfessor(professorId);
        }
        return studentService.list(companyId);
    }

    @GetMapping("/{id}/grades")
    @Operation(summary = "Notas publicadas de un estudiante")
    public List<GradeResponse> grades(@PathVariable UUID id) {
        return gradeService.recentForStudent(id);
    }
}

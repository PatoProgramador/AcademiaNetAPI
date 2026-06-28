package com.academianet.demo.controller;

import com.academianet.demo.dto.CourseResponse;
import com.academianet.demo.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Cursos", description = "Catálogo de cursos del periodo")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Listar cursos", description = "Incluye el promedio calculado de cada curso.")
    public List<CourseResponse> list(@RequestParam(required = false) UUID companyId) {
        return courseService.list(companyId);
    }
}

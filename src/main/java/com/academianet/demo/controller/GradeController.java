package com.academianet.demo.controller;

import com.academianet.demo.dto.GradeResponse;
import com.academianet.demo.dto.GradeUpdateRequest;
import com.academianet.demo.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@Tag(name = "Notas", description = "Registro de calificaciones (panel profesor)")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una nota", description = "Cambia el valor y/o la visibilidad (published).")
    public GradeResponse update(@PathVariable UUID id, @Valid @RequestBody GradeUpdateRequest request) {
        return gradeService.update(id, request);
    }
}

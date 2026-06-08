package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.GradeResponse;
import com.academiaNetAPI.demo.dto.GradeUpdateRequest;
import com.academiaNetAPI.demo.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PutMapping("/{id}")
    public GradeResponse update(@PathVariable UUID id, @Valid @RequestBody GradeUpdateRequest request) {
        return gradeService.update(id, request);
    }
}

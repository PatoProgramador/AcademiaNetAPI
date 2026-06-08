package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.CourseResponse;
import com.academiaNetAPI.demo.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> list(@RequestParam(required = false) UUID companyId) {
        return courseService.list(companyId);
    }
}

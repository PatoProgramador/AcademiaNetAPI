package com.academianet.demo.service;

import com.academianet.demo.dto.GradeResponse;
import com.academianet.demo.dto.GradeUpdateRequest;
import com.academianet.demo.entity.Evaluation;
import com.academianet.demo.entity.Grade;
import com.academianet.demo.entity.User;
import com.academianet.demo.exception.NotFoundException;
import com.academianet.demo.repository.GradeRepository;
import com.academianet.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    public GradeService(GradeRepository gradeRepository, UserRepository userRepository) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> recentForStudent(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado: " + studentId));
        return gradeRepository.findByEnrollment_StudentAndPublishedTrue(student).stream()
                .sorted(Comparator.comparing(Grade::getRecordDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(GradeService::toResponse)
                .toList();
    }

    @Transactional
    public GradeResponse update(UUID gradeId, GradeUpdateRequest request) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new NotFoundException("Nota no encontrada: " + gradeId));
        grade.setValue(request.value());
        if (request.published() != null) {
            grade.setPublished(request.published());
        }
        grade.setRecordDate(OffsetDateTime.now());
        return toResponse(gradeRepository.save(grade));
    }

    public static GradeResponse toResponse(Grade grade) {
        Evaluation evaluation = grade.getEvaluation();
        String subjectName = grade.getEnrollment().getCourse().getSubject().getName();
        return new GradeResponse(
                grade.getId(),
                evaluation.getName(),
                subjectName,
                evaluation.getType() == null ? null : evaluation.getType().name(),
                evaluation.getDate(),
                grade.getValue(),
                grade.getMaxValue(),
                grade.isPublished()
        );
    }
}

package ru.vmk.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.SubjectVisitListDto;
import ru.vmk.attendance.dto.TeacherSubjectVisitListDto;
import ru.vmk.attendance.service.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_GROUP_HEAD') || hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getSubjects(@RequestParam Integer course, @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(subjectService.getSubjects(course, auth.getId()));
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> getTeacherSubjects(@AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(subjectService.getTeacherSubjects(auth.getId()));
    }

    @GetMapping("/{id}/visit-list")
    public ResponseEntity<?> getSubjectVisitList(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(subjectService.getSubjectVisitList(id, auth));
    }

    @GetMapping("/teacher/{id}/visit-list")
    public ResponseEntity<?> getTeacherSubjectVisitList(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(subjectService.getTeacherSubjectVisitList(id, auth));
    }

    @PostMapping("/{id}/visit-list")
    public ResponseEntity<?> saveSubjectVisitList(@PathVariable Long id, @RequestBody SubjectVisitListDto dto, @AuthenticationPrincipal UserDetailsImpl auth) {
        subjectService.saveSubjectVisitList(id, dto, auth);
        return ResponseEntity.ok("");
    }

    @PostMapping("/teacher/{id}/visit-list")
    public ResponseEntity<?> saveTeacherSubjectVisitList(@PathVariable Long id, @RequestBody TeacherSubjectVisitListDto dto, @AuthenticationPrincipal UserDetailsImpl auth) {
        subjectService.saveTeacherSubjectVisitList(id, dto, auth);
        return ResponseEntity.ok("");
    }

    @GetMapping("/generate")
    public byte[] generateReport(@RequestParam Integer course, @RequestParam List<Long> groupIds) throws Exception {
        return subjectService.generateReport(course, groupIds);
    }
}

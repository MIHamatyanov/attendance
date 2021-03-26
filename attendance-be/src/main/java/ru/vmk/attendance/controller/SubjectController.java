package ru.vmk.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.SubjectVisitListDto;
import ru.vmk.attendance.service.SubjectService;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<?> getSubjects(@RequestParam Integer course, @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(subjectService.getSubjects(course, auth.getId()));
    }

    @GetMapping("/{id}/visit-list")
    public ResponseEntity<?> getSubjectVisitList(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(subjectService.getSubjectVisitList(id, auth));
    }

    @PostMapping("/{id}/visit-list")
    public ResponseEntity<?> saveSubjectVisitList(@PathVariable Long id, @RequestBody SubjectVisitListDto dto, @AuthenticationPrincipal UserDetailsImpl auth) {
        subjectService.saveSubjectVisitList(id, dto, auth);
        return ResponseEntity.ok("");
    }
}

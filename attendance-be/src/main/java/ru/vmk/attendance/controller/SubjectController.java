package ru.vmk.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vmk.attendance.config.security.UserDetailsImpl;
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
}

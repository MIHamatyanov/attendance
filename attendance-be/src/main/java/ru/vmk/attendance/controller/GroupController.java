package ru.vmk.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.model.Group;
import ru.vmk.attendance.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<?> getCurrentUserInfo(@RequestParam Long course, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Group> groups = groupService.getGroupsByCourseGreater(course);

        return ResponseEntity.ok(groups);
    }
}

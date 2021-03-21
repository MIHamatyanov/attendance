package ru.vmk.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.ChangePasswordDto;
import ru.vmk.attendance.dto.UserDto;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> getCurrentUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userService.getUser(userDetails.getId());

        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto, @AuthenticationPrincipal UserDetailsImpl auth) {
        userService.changePassword(dto, auth.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/photo")
    public ResponseEntity<?> changePhoto(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetailsImpl auth) {
        if (file != null) {
            User user = userService.changePhoto(file, auth.getId());
            return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

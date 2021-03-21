package ru.vmk.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.service.FileService;
import ru.vmk.attendance.service.UserService;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    @GetMapping(value = "/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getFile(@PathVariable("filename") String filename, @AuthenticationPrincipal UserDetailsImpl auth) {
        return fileService.getPhotoByteArray(filename, userService.getUser(auth.getId()));
    }
}

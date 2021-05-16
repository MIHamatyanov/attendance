package ru.vmk.attendance.service;

import org.springframework.web.multipart.MultipartFile;
import ru.vmk.attendance.model.User;

public interface FileService {
    String savePhoto(MultipartFile file);

    byte[] getPhotoByteArray(String filename, User authUser);
}

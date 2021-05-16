package ru.vmk.attendance.service;

import org.springframework.web.multipart.MultipartFile;
import ru.vmk.attendance.dto.ChangePasswordDto;
import ru.vmk.attendance.model.User;

import java.util.List;

public interface UserService {
    User getUser(Long id);

    void changePassword(ChangePasswordDto dto, Long authId);

    User changePhoto(MultipartFile file, Long authId);

    List<User> getGroupList(Long id);
}

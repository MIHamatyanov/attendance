package ru.vmk.attendance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vmk.attendance.dto.ChangePasswordDto;
import ru.vmk.attendance.exception.PasswordNotMatchException;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public void changePassword(ChangePasswordDto dto, Long authId) {
        User authUser = getUser(authId);

        if (!passwordEncoder.matches(dto.getOldPassword(), authUser.getPassword())) {
            throw new PasswordNotMatchException();
        }

        authUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(authUser);
    }

    public User changePhoto(MultipartFile file, Long authId) {
        User authUser = getUser(authId);

        String photoUrl = fileService.savePhoto(file);
        authUser.setPhotoUrl(photoUrl);
        authUser = userRepository.save(authUser);

        return authUser;
    }
}

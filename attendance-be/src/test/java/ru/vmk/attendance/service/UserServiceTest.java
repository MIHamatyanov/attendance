package ru.vmk.attendance.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vmk.attendance.dto.ChangePasswordDto;
import ru.vmk.attendance.exception.PasswordNotMatchException;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.repository.UserRepository;
import ru.vmk.attendance.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UserServiceTest {
    private static UserRepository userRepository;
    private static PasswordEncoder passwordEncoder;
    private static FileService fileService;
    private static UserServiceImpl userService;

    @BeforeAll
    public static void init() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        fileService = Mockito.mock(FileService.class);

        userService = new UserServiceImpl(userRepository, passwordEncoder, fileService);
    }

    @Test
    void getUser_Success() {
        //Given
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("test");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        //When
        User user = userService.getUser(1L);

        //Then
        Assertions.assertNotNull(user);
        Assertions.assertEquals(1L, user.getId());
        Assertions.assertEquals("test", user.getName());
    }

    @Test
    void getUser_Failure() {
        //Given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        //When - Then
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void changePassword_Success() {
        //Given
        String encoded = passwordEncoder.encode("12345");
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("test");
        testUser.setPassword(encoded);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("12345");
        changePasswordDto.setNewPassword("123");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        //When - Then
        Assertions.assertDoesNotThrow(() -> userService.changePassword(changePasswordDto, 1L));
    }

    @Test
    void changePassword_Failure() {
        //Given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encoded = passwordEncoder.encode("qweqwe");
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("test");
        testUser.setPassword(encoded);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("12345");
        changePasswordDto.setNewPassword("123");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        //When - Then
        Assertions.assertThrows(PasswordNotMatchException.class, () -> userService.changePassword(changePasswordDto, 1L));
    }

    @Test
    void changePhoto() {
        //Given
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("test");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
        Mockito.when(fileService.savePhoto(null)).thenReturn("1");
        //When
        User user = userService.changePhoto(null, 1L);

        //Then
        Assertions.assertEquals("1", user.getPhotoUrl());
    }

    @Test
    void getGroupList() {
        //Given
        Mockito.when(userRepository.getAllByGroupId(1L)).thenReturn(Collections.emptyList());

        //When
        List<User> users = userService.getGroupList(1L);

        //Then
        Assertions.assertEquals(0, users.size());
    }
}

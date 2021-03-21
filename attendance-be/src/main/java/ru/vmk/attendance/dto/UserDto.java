package ru.vmk.attendance.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vmk.attendance.model.Group;
import ru.vmk.attendance.model.enums.Role;

import java.time.LocalDate;

@Getter
@Setter
public class UserDto {
    private Long id;

    private String email;
    private Role role;

    private String surname;
    private String name;
    private String patronymic;

    private String citizenship;
    private LocalDate birthDate;
    private String institute;

    private Group group;

    private String photoUrl;
}

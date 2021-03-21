package ru.vmk.attendance.model;

import lombok.Getter;
import lombok.Setter;
import ru.vmk.attendance.model.enums.Role;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "app_user",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String surname;
    private String name;
    private String patronymic;

    private LocalDate birthDate;
    private String citizenship;
    private String institute;
    @OneToOne
    private Group group;

    private String photoUrl;
}

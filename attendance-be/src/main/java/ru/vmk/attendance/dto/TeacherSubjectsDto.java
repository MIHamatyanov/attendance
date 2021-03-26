package ru.vmk.attendance.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vmk.attendance.model.Subject;

import java.util.List;

@Getter
@Setter
public class TeacherSubjectsDto {
    private int weekDay;
    private List<Subject> subjects;
}

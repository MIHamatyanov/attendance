package ru.vmk.attendance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.vmk.attendance.model.Subject;

import java.util.List;

@Getter
@Setter
@Builder
public class SubjectsDto {
    private List<Subject> firstSemester;
    private List<Subject> secondSemester;
}

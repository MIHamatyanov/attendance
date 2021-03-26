package ru.vmk.attendance.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vmk.attendance.model.Subject;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SubjectVisitListDto {
    private LocalDate date;
    private Subject subject;
    private List<UserVisitListDto> userVisitList;
}

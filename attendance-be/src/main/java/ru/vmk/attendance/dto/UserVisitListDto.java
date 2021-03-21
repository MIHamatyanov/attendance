package ru.vmk.attendance.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vmk.attendance.model.VisitList;

import java.util.List;

@Getter
@Setter
public class UserVisitListDto {
    private UserDto user;
    private List<VisitList> visitList;
}

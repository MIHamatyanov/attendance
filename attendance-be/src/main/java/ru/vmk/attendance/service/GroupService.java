package ru.vmk.attendance.service;

import ru.vmk.attendance.model.Group;

import java.util.List;

public interface GroupService {
    List<Group> getGroupsByCourseGreater(Long course);
}

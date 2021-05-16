package ru.vmk.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vmk.attendance.model.Group;
import ru.vmk.attendance.repository.GroupRepository;
import ru.vmk.attendance.service.GroupService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;

    @Override
    public List<Group> getGroupsByCourseGreater(Long course) {
        return groupRepository.findAllByCourseGreaterThan(course);
    }
}

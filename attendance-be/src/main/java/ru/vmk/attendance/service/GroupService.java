package ru.vmk.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vmk.attendance.model.Group;
import ru.vmk.attendance.repository.GroupRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<Group> getGroupsByCourseGreater(Long course) {
        return groupRepository.findAllByCourseGreaterThan(course);
    }
}

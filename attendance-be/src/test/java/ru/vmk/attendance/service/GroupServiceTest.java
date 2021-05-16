package ru.vmk.attendance.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vmk.attendance.model.Group;
import ru.vmk.attendance.repository.GroupRepository;
import ru.vmk.attendance.service.impl.GroupServiceImpl;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class GroupServiceTest {
    private static GroupRepository groupRepository;
    private static GroupService groupService;

    @BeforeAll
    public static void init() {
        groupRepository = Mockito.mock(GroupRepository.class);

        groupService = new GroupServiceImpl(groupRepository);
    }

    @Test
    void getGroupsByCourseGreater() {
        //Given
        Mockito.when(groupRepository.findAllByCourseGreaterThan(1L)).thenReturn(Collections.emptyList());

        //When
        List<Group> groupList = groupService.getGroupsByCourseGreater(1L);

        //Then
        Assertions.assertEquals(0, groupList.size());
    }
}

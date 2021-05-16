package ru.vmk.attendance.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vmk.attendance.dto.SubjectsDto;
import ru.vmk.attendance.dto.TeacherSubjectsDto;
import ru.vmk.attendance.model.Schedule;
import ru.vmk.attendance.model.Subject;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.repository.ScheduleRepository;
import ru.vmk.attendance.repository.SubjectRepository;
import ru.vmk.attendance.repository.VisitListRepository;
import ru.vmk.attendance.service.impl.SubjectServiceImpl;
import ru.vmk.attendance.utils.NullableModelMapper;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SubjectServiceTest {
    private static SubjectRepository subjectRepository;
    private static VisitListRepository visitListRepository;
    private static ScheduleRepository scheduleRepository;
    private static UserService userService;
    private static SubjectService subjectService;

    @BeforeAll
    public static void init() {
        subjectRepository = Mockito.mock(SubjectRepository.class);
        visitListRepository = Mockito.mock(VisitListRepository.class);
        scheduleRepository = Mockito.mock(ScheduleRepository.class);
        userService = Mockito.mock(UserService.class);
        ModelMapper modelMapper = new NullableModelMapper();

        subjectService = new SubjectServiceImpl(subjectRepository, visitListRepository, scheduleRepository, modelMapper, userService);
    }

    @Test
    void getSubjects() {
        //Given
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("test");
        testUser.setGroup(null);
        Mockito.when(userService.getUser(1L)).thenReturn(testUser);
        List<Subject> subjects = new ArrayList<>();
        Subject firstSubject = new Subject();
        firstSubject.setSemester(1);
        Subject secondSubject = new Subject();
        secondSubject.setSemester(2);
        Subject thirdSubject = new Subject();
        thirdSubject.setSemester(2);
        subjects.add(firstSubject);
        subjects.add(secondSubject);
        subjects.add(thirdSubject);
        Mockito.when(subjectRepository.findAllByCourseAndGroupOrderByCourseAscSemesterAsc(1, null)).thenReturn(subjects);

        //When
        SubjectsDto dto = subjectService.getSubjects(1, 1L);

        //Then
        Assertions.assertEquals(1, dto.getFirstSemester().size());
        Assertions.assertEquals(2, dto.getSecondSemester().size());
    }

    @Test
    void getTeacherSubjects() {
        //Given
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("test");
        testUser.setGroup(null);
        Mockito.when(userService.getUser(1L)).thenReturn(testUser);
        List<Schedule> schedules = new ArrayList<>();
        Subject firstSubject = new Subject();
        firstSubject.setSemester(1);
        Subject secondSubject = new Subject();
        secondSubject.setSemester(2);
        Subject thirdSubject = new Subject();
        thirdSubject.setSemester(2);

        Schedule schedule = new Schedule();
        schedule.setWeekDay(1);
        schedule.setSubject(firstSubject);
        schedules.add(schedule);
        schedule = new Schedule();
        schedule.setWeekDay(2);
        schedule.setSubject(secondSubject);
        schedules.add(schedule);
        schedule = new Schedule();
        schedule.setWeekDay(1);
        schedule.setSubject(thirdSubject);
        schedules.add(schedule);

        Mockito.when(scheduleRepository.findAllBySubjectTeacherIdOrderByWeekDayAscStartTimeAsc(1L)).thenReturn(schedules);

        //When
        List<TeacherSubjectsDto> teacherSubjects = subjectService.getTeacherSubjects(1L);

        //Then
        Assertions.assertEquals(7, teacherSubjects.size());
        Assertions.assertEquals(1, teacherSubjects.get(1).getSubjects().size());
        Assertions.assertEquals(0, teacherSubjects.get(2).getSubjects().size());
    }
}

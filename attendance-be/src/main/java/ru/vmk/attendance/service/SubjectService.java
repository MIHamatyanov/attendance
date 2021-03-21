package ru.vmk.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vmk.attendance.dto.SubjectsDto;
import ru.vmk.attendance.model.Subject;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserService userService;

    public SubjectsDto getSubjects(int course, Long authId) {
        User authUser = userService.getUser(authId);

        List<Subject> subjects = subjectRepository.findAllByCourseAndGroupOrderByCourseAscSemesterAsc(course, authUser.getGroup());

        return SubjectsDto.builder()
                .firstSemester(subjects.stream().filter(subject -> subject.getSemester() == 1).collect(Collectors.toList()))
                .secondSemester(subjects.stream().filter(subject -> subject.getSemester() == 2).collect(Collectors.toList()))
                .build();
    }
}

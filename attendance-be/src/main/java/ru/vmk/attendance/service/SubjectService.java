package ru.vmk.attendance.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.SubjectVisitListDto;
import ru.vmk.attendance.dto.SubjectsDto;
import ru.vmk.attendance.dto.UserDto;
import ru.vmk.attendance.dto.UserVisitListDto;
import ru.vmk.attendance.model.Subject;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.model.VisitList;
import ru.vmk.attendance.repository.SubjectRepository;
import ru.vmk.attendance.repository.VisitListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final VisitListRepository visitListRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public SubjectsDto getSubjects(int course, Long authId) {
        User authUser = userService.getUser(authId);

        List<Subject> subjects = subjectRepository.findAllByCourseAndGroupOrderByCourseAscSemesterAsc(course, authUser.getGroup());

        return SubjectsDto.builder()
                .firstSemester(subjects.stream().filter(subject -> subject.getSemester() == 1).collect(Collectors.toList()))
                .secondSemester(subjects.stream().filter(subject -> subject.getSemester() == 2).collect(Collectors.toList()))
                .build();
    }

    public SubjectVisitListDto getSubjectVisitList(Long subjectId, UserDetailsImpl auth) {
        User authUser = userService.getUser(auth.getId());
        //TODO проверка прав

        Subject subject = subjectRepository.getById(subjectId);
        List<VisitList> visitList = visitListRepository.getAllBySubjectId(subjectId);
        List<User> groupList = userService.getGroupList(subject.getGroup().getId());

        SubjectVisitListDto dto = new SubjectVisitListDto();
        dto.setSubject(subject);

        List<UserVisitListDto> userVisitListDtos = new ArrayList<>();
        groupList.forEach(user -> {
            List<VisitList> userVisitList = visitList.stream().filter(vl -> vl.getStudent().getId().equals(user.getId())).collect(Collectors.toList());
            UserVisitListDto userVisitListDto = new UserVisitListDto();
            userVisitListDto.setUser(modelMapper.map(user, UserDto.class));
            userVisitListDto.setVisitList(userVisitList);
            userVisitListDtos.add(userVisitListDto);
        });

        dto.setUserVisitList(userVisitListDtos);

        return dto;
    }
}

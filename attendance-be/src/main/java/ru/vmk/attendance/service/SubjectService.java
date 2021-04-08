package ru.vmk.attendance.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.*;
import ru.vmk.attendance.model.Schedule;
import ru.vmk.attendance.model.Subject;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.model.VisitList;
import ru.vmk.attendance.repository.ScheduleRepository;
import ru.vmk.attendance.repository.SubjectRepository;
import ru.vmk.attendance.repository.VisitListRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final VisitListRepository visitListRepository;
    private final ScheduleRepository scheduleRepository;
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

    public List<TeacherSubjectsDto> getTeacherSubjects(Long authId) {
        User authUser = userService.getUser(authId);

        List<Schedule> schedule = scheduleRepository.findAllBySubjectTeacherIdOrderByWeekDayAscStartTimeAsc(authId);

        List<TeacherSubjectsDto> dtoList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            TeacherSubjectsDto dto = new TeacherSubjectsDto();
            dto.setWeekDay(i);
            int finalI = i;
            dto.setSubjects(schedule.stream().filter(s -> s.getWeekDay() == finalI).map(Schedule::getSubject).collect(Collectors.toList()));
            dtoList.add(dto);
        }

        return dtoList;
    }

    public SubjectVisitListDto getSubjectVisitList(Long subjectId, UserDetailsImpl auth) {
        User authUser = userService.getUser(auth.getId());
        //TODO проверка прав

        Subject subject = subjectRepository.getById(subjectId);
        List<VisitList> visitList = visitListRepository.getAllBySubjectIdOrderByDate(subjectId);
        List<User> groupList = userService.getGroupList(subject.getGroup().getId());

        SubjectVisitListDto dto = new SubjectVisitListDto();
        dto.setSubject(subject);

        fillVisitList(visitList, groupList, dto);

        return dto;
    }

    private void fillVisitList(List<VisitList> visitList, List<User> groupList, SubjectVisitListDto dto) {
        List<UserVisitListDto> userVisitListDtos = new ArrayList<>();
        groupList.forEach(user -> {
            List<VisitList> userVisitList = visitList.stream().filter(vl -> vl.getStudent().getId().equals(user.getId())).collect(Collectors.toList());
            UserVisitListDto userVisitListDto = new UserVisitListDto();
            userVisitListDto.setUser(modelMapper.map(user, UserDto.class));
            userVisitListDto.setVisitList(userVisitList);
            userVisitListDto.setNotVisitCount(userVisitList.stream().filter(vl -> !vl.isPresence()).count());
            userVisitListDto.setMarksSum(userVisitList.stream().flatMapToInt(vl -> {
                int mark;
                try {
                    mark = Integer.parseInt(vl.getMark());
                } catch (Exception e) {
                    mark = 0;
                }
                return IntStream.of(mark);
            }).sum());
            userVisitListDtos.add(userVisitListDto);
        });

        dto.setUserVisitList(userVisitListDtos);
    }

    public TeacherSubjectVisitListDto getTeacherSubjectVisitList(Long subjectId, UserDetailsImpl auth) {
        User authUser = userService.getUser(auth.getId());
        //TODO проверка прав

        Subject subject = subjectRepository.getById(subjectId);
        List<VisitList> visitList = visitListRepository.getAllBySubjectIdOrderByDate(subjectId);
        List<User> groupList = userService.getGroupList(subject.getGroup().getId());
        List<User> firstSubGroup = groupList.stream().filter(u -> u.getSubGroup() == 1).collect(Collectors.toList());
        List<User> secondSubGroup = groupList.stream().filter(u -> u.getSubGroup() == 2).collect(Collectors.toList());

        TeacherSubjectVisitListDto teacherSubjectVisitListDto = new TeacherSubjectVisitListDto();
        teacherSubjectVisitListDto.setSubject(subject);

        SubjectVisitListDto firstSubGroupDto = new SubjectVisitListDto();
        fillVisitList(visitList, firstSubGroup, firstSubGroupDto);

        SubjectVisitListDto secondSubGroupDto = new SubjectVisitListDto();
        fillVisitList(visitList, secondSubGroup, secondSubGroupDto);

        List<SubjectVisitListDto> subGroups = new ArrayList<>();
        subGroups.add(firstSubGroupDto);
        subGroups.add(secondSubGroupDto);
        teacherSubjectVisitListDto.setSubGroups(subGroups);

        return teacherSubjectVisitListDto;
    }

    @Transactional
    public void saveSubjectVisitList(Long subjectId, SubjectVisitListDto dto, UserDetailsImpl auth) {
        User authUser = userService.getUser(auth.getId());
        //TODO проверка прав

        saveVisitList(subjectId, dto, false);
    }

    @Transactional
    public void saveTeacherSubjectVisitList(Long subjectId, TeacherSubjectVisitListDto teacherDto, UserDetailsImpl auth) {
        User authUser = userService.getUser(auth.getId());
        //TODO проверка прав

        for (SubjectVisitListDto subGroup : teacherDto.getSubGroups()) {
            saveVisitList(subjectId, subGroup, true);
        }
    }

    private void saveVisitList(Long subjectId, SubjectVisitListDto dto, boolean isTeacher) {
        LocalDate date = dto.getDate();
        Subject subject = subjectRepository.getById(subjectId);
        List<UserVisitListDto> userVisitList = dto.getUserVisitList();
        for (UserVisitListDto userVisitListDto : userVisitList) {
            for (VisitList visitList : userVisitListDto.getVisitList()) {
                VisitList dbVisitList = visitListRepository.getById(visitList.getId());
                String mark = visitList.getMark();
                dbVisitList.setPresence(mark == null || (!mark.equalsIgnoreCase("н") && !mark.equals("-")));
                dbVisitList.setMark(dbVisitList.isPresence() ? isTeacher ? visitList.getMark() : dbVisitList.getMark() : "н");
            }

            if (date != null) {
                VisitList visitList = new VisitList();
                visitList.setSubject(subject);
                visitList.setStudent(userService.getUser(userVisitListDto.getUser().getId()));
                visitList.setDate(date);
                String mark = userVisitListDto.getMark();
                visitList.setPresence(mark == null || (!mark.equalsIgnoreCase("н") && !mark.equals("-")));
                visitList.setMark(visitList.isPresence() ? isTeacher ? mark : "" : "н");
                visitListRepository.save(visitList);
            }
        }
    }
}

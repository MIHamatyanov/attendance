package ru.vmk.attendance.service;

import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.SubjectVisitListDto;
import ru.vmk.attendance.dto.SubjectsDto;
import ru.vmk.attendance.dto.TeacherSubjectVisitListDto;
import ru.vmk.attendance.dto.TeacherSubjectsDto;

import java.util.List;

public interface SubjectService {
    SubjectsDto getSubjects(int course, Long authId);

    List<TeacherSubjectsDto> getTeacherSubjects(Long authId);

    SubjectVisitListDto getSubjectVisitList(Long subjectId, UserDetailsImpl auth);

    TeacherSubjectVisitListDto getTeacherSubjectVisitList(Long subjectId, UserDetailsImpl auth);

    void saveSubjectVisitList(Long subjectId, SubjectVisitListDto dto, UserDetailsImpl auth);

    void saveTeacherSubjectVisitList(Long subjectId, TeacherSubjectVisitListDto teacherDto, UserDetailsImpl auth);

    byte[] generateReport(Integer course, List<Long> groupIds) throws Exception;
}

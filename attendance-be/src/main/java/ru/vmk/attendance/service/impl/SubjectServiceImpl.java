package ru.vmk.attendance.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vmk.attendance.config.security.UserDetailsImpl;
import ru.vmk.attendance.dto.*;
import ru.vmk.attendance.model.Schedule;
import ru.vmk.attendance.model.Subject;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.model.VisitList;
import ru.vmk.attendance.repository.ScheduleRepository;
import ru.vmk.attendance.repository.SubjectRepository;
import ru.vmk.attendance.repository.VisitListRepository;
import ru.vmk.attendance.service.SubjectService;
import ru.vmk.attendance.service.UserService;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    @Value("${custom.itext.font.filePath}")
    private String fontPath;

    private final SubjectRepository subjectRepository;
    private final VisitListRepository visitListRepository;
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public SubjectsDto getSubjects(int course, Long authId) {
        User authUser = userService.getUser(authId);

        List<Subject> subjects = subjectRepository.findAllByCourseAndGroupOrderByCourseAscSemesterAsc(course, authUser.getGroup());

        return SubjectsDto.builder()
                .firstSemester(subjects.stream().filter(subject -> subject.getSemester() == 1).collect(Collectors.toList()))
                .secondSemester(subjects.stream().filter(subject -> subject.getSemester() == 2).collect(Collectors.toList()))
                .build();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    @Transactional
    public void saveSubjectVisitList(Long subjectId, SubjectVisitListDto dto, UserDetailsImpl auth) {
        User authUser = userService.getUser(auth.getId());
        //TODO проверка прав

        saveVisitList(subjectId, dto, false);
    }

    @Override
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

    @Override
    public byte[] generateReport(Integer course, List<Long> groupIds) throws Exception {
        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();
        List<Subject> subjects = subjectRepository.findAllByCourseAndGroupIdIn(course, groupIds);
        subjects.sort(Comparator.comparingInt(Subject::getSemester));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        Document document = new Document();
        PdfWriter.getInstance(document, resultBaos);

        document.open();

        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 8, Font.NORMAL);

        for (Subject subject : subjects) {
            Paragraph tableHeader = new Paragraph(
                    subject.getGroup().getName()
                            + " " + subject.getName()
                            + " " + subject.getCourse() + " курс,"
                            + " " + subject.getSemester() + " семестр.",
                    font);
            tableHeader.setSpacingAfter(10);

            document.add(tableHeader);
            List<VisitList> visitList = visitListRepository.getAllBySubjectIdOrderByDate(subject.getId());
            List<User> groupList = userService.getGroupList(subject.getGroup().getId());

            SubjectVisitListDto dto = new SubjectVisitListDto();
            dto.setSubject(subject);

            fillVisitList(visitList, groupList, dto);

            List<String> headers = new ArrayList<>();
            headers.add("ФИО");

            if (!dto.getUserVisitList().isEmpty()) {
                for (VisitList list : dto.getUserVisitList().get(0).getVisitList()) {
                    headers.add(list.getDate().format(formatter));
                }
            }
            headers.add("Н");
            headers.add("Баллы");

            PdfPTable table = new PdfPTable(headers.size());
            table.setSpacingAfter(20);
            int[] columnWidth = new int[headers.size()];
            columnWidth[0] = 15;
            for (int i = 1; i < columnWidth.length; i++) {
                columnWidth[i] = 5;
            }
            table.setWidths(columnWidth);
            addTableHeader(table, headers, font);

            for (UserVisitListDto userVisitListDto : dto.getUserVisitList()) {
                List<String> tableCellsData = new ArrayList<>();
                tableCellsData.add(userVisitListDto.getUser().getSurname() + userVisitListDto.getUser().getName().charAt(0) + ". " + userVisitListDto.getUser().getPatronymic().charAt(0) + ".");

                for (VisitList list : userVisitListDto.getVisitList()) {
                    tableCellsData.add(list.getMark());
                }

                tableCellsData.add(String.valueOf(userVisitListDto.getNotVisitCount()));
                tableCellsData.add(String.valueOf(userVisitListDto.getMarksSum()));

                addRows(table, tableCellsData, font);
            }

            document.add(table);
        }
        document.close();

        return resultBaos.toByteArray();
    }

    private static void addTableHeader(PdfPTable table, List<String> headers, Font font) {
        headers.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setPhrase(new Phrase(columnTitle, font));
            table.addCell(header);
        });
    }

    private static void addRows(PdfPTable table, List<String> cellsData, Font font) {
        for (String cellData : cellsData) {
            table.addCell(new Phrase(cellData, font));
        }
    }
}

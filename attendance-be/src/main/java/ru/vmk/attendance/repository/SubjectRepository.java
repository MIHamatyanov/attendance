package ru.vmk.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vmk.attendance.model.Group;
import ru.vmk.attendance.model.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByCourseAndGroupOrderByCourseAscSemesterAsc(int course, Group group);

    Subject getById(Long subjectId);
}

package ru.vmk.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vmk.attendance.model.VisitList;

import java.util.List;

@Repository
public interface VisitListRepository extends JpaRepository<VisitList, Long> {
    List<VisitList> getAllBySubjectId(Long subjectId);
}

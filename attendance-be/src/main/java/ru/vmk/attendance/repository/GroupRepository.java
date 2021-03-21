package ru.vmk.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vmk.attendance.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}

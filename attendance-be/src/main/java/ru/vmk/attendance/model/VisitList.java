package ru.vmk.attendance.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class VisitList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Subject subject;
    private LocalDate date;
    @ManyToOne
    private User student;
    private byte mark;
    private boolean presence;
}

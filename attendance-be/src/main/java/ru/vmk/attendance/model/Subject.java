package ru.vmk.attendance.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToOne
    private User teacher;
    @ManyToOne
    private Group group;
    private int semester;
    private int course;
    @OneToMany(targetEntity = Schedule.class,
            mappedBy = "subject",
            fetch = FetchType.LAZY)
    private List<Schedule> schedule;
}

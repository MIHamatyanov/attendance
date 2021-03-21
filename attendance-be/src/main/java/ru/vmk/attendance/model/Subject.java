package ru.vmk.attendance.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    //TODO Расписание - List<Schedule> schedule Schedule - время, четность недели, день недели
}

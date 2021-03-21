package ru.vmk.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "app_group",
        uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@JsonIgnoreProperties({"groupHead"})
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User groupHead;
    private String name;
    private Long course;
}


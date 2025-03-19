package com.calendarugr.schedule_consumer_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // to prevent lazy initialization exception
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "grade"}, name = "unique_name_grade_constraint")}) // means that the combination of name and grade must be unique
public class Subject{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String name;

    @Size(max = 50)
    @Column(length = 50)
    private String academic_course;

    @Size(max = 30)
    @Column(length = 30)
    private String year;

    @Size(max = 30)
    @Column(length = 30)
    private String semester;

    @Size(max = 100)
    @Column(length = 100)
    private String type;

    @Size(max = 450)
    @Column(length = 450)
    private String department;

    @Size(max = 255)
    @Column(length = 255)
    private String url;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;
}

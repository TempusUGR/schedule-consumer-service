package com.calendarugr.schedule_consumer_service.entities;

import java.util.Arrays;
import java.util.List;

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
@Table( name = "subject_group", uniqueConstraints = {@UniqueConstraint(columnNames = {"name","subject"}, name = "unique_name_subject_constraint")}) 
public class Group{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column(length = 150)
    private String name;

    @NotNull
    @Size(max = 500)
    @Column(length = 500)
    private String teacher;

    @ManyToOne
    @JoinColumn(name= "subject", nullable = false)
    private Subject subject;

    // Método para obtener la lista de profesores
    public List<String> getTeachersList() {
        return Arrays.asList(teacher.split(","));
    }

    // Método para establecer la lista de profesores
    public void setTeachersList(List<String> teachersList) {
        this.teacher = String.join(",", teachersList);
    }

}

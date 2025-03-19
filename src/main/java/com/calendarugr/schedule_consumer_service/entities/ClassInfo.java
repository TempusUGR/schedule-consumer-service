package com.calendarugr.schedule_consumer_service.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // to prevent lazy initialization exception
public class ClassInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String classroom;

    @NotNull
    @Size(max = 50)
    @Column(unique = false, length = 50)
    private String day;

    @NotNull
    @Column(nullable = false)
    private LocalDate initDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate finishDate;

    @NotNull
    @Column(nullable = false)
    private LocalTime initHour;

    @NotNull
    @Column(nullable = false)
    private LocalTime finishHour;

    @ManyToOne 
    @JoinColumn(name= "subject_group", nullable = false)
    private Group subjectGroup;

}

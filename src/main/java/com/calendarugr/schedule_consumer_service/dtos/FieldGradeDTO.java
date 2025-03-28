package com.calendarugr.schedule_consumer_service.dtos;

import java.util.List;

import lombok.Data;

@Data
public class FieldGradeDTO {

    private String field;
    private List<String> grades;

    public FieldGradeDTO(String field, List<String> grades) {
        this.field = field;
        this.grades = grades;
    }

}
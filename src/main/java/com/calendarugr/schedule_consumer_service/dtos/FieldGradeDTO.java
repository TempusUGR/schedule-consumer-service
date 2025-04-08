package com.calendarugr.schedule_consumer_service.dtos;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class FieldGradeDTO {

    private String field;
    private List<Map<String, String>> grades;

    public FieldGradeDTO(String field, List<Map<String, String>> grades) {
        this.field = field;
        this.grades = grades;
    }
}
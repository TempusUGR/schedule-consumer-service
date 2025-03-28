package com.calendarugr.schedule_consumer_service.dtos;

import java.util.List;

import lombok.Data;

@Data
public class SubjectGroupsDTO {

    private String subject;
    private List<String> groups;

    public SubjectGroupsDTO(String name, List<String> list) {
        this.subject = name;
        this.groups = list;
    }

}

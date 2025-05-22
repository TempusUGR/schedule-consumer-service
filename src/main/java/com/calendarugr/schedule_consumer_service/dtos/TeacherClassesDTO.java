package com.calendarugr.schedule_consumer_service.dtos;

import java.util.List;

import com.calendarugr.schedule_consumer_service.dtos.SubscriptionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TeacherClassesDTO {
    private String teacherName;
    private List<SubscriptionDTO> classes; 
}

package com.calendarugr.schedule_consumer_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.calendarugr.schedule_consumer_service.services.ScheduleConsumerService;

@RestController
@RequestMapping("/schedule-calendar-consumer")
public class ScheduleConsumerController {

    @Autowired
    private ScheduleConsumerService scheduleConsumerService;

    @GetMapping("/classes")
    public List<ClassInfo> getCLasses(@RequestParam String grade, @RequestParam String subject, @RequestParam String group) {
        return scheduleConsumerService.getClasses(grade, subject, group);
    }
}

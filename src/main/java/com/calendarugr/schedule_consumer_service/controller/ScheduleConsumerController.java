package com.calendarugr.schedule_consumer_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calendarugr.schedule_consumer_service.dtos.ClassDTO;
import com.calendarugr.schedule_consumer_service.dtos.ExtraClassDTO;
import com.calendarugr.schedule_consumer_service.dtos.FieldGradeDTO;
import com.calendarugr.schedule_consumer_service.dtos.SubjectGroupsDTO;
import com.calendarugr.schedule_consumer_service.dtos.SubscriptionDTO;
import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.calendarugr.schedule_consumer_service.services.ScheduleConsumerService;

@RestController
@RequestMapping("/schedule-consumer")
public class ScheduleConsumerController {

    @Autowired
    private ScheduleConsumerService scheduleConsumerService;

    @GetMapping("/classes-from-group")
    public ResponseEntity<?> getClasses(@RequestParam String grade, @RequestParam String subject, @RequestParam String group) {
        List<ClassInfo> classes = scheduleConsumerService.getClasses(grade, subject, group);
        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron clases para el grupo " + group + " de la materia " + subject + " del grado " + grade);
        }
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/grades")
    public ResponseEntity<?> getGrades() {
        List<FieldGradeDTO> grades = scheduleConsumerService.getGrades();
        if (grades.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron grados");
        }
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/subjects-groups")
    public ResponseEntity<?> getSubjectsAndGroups(@RequestParam String grade) {
        List<SubjectGroupsDTO> subjectsGroups = scheduleConsumerService.getSubjectsAndGroups(grade);
        if (subjectsGroups.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron materias y grupos para el grado " + grade);
        }
        return ResponseEntity.ok(subjectsGroups);
    }

    @PostMapping("/classes-per-subscriptions")
    public ResponseEntity<?> getClassesPerSubscriptions(@RequestBody List<SubscriptionDTO> subscriptions) {
        List<ClassDTO> classes = scheduleConsumerService.getClassesPerSubscriptions(subscriptions);
        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron clases para las suscripciones");
        }
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/validate-subscription")
    public ResponseEntity<Boolean> validateSubscription(@RequestBody SubscriptionDTO subscription) {
        boolean isValid = scheduleConsumerService.validateSubscription(subscription);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/validate-extra-class")
    public ResponseEntity<Boolean> validateExtraClass(@RequestBody ExtraClassDTO extraClass) {
        boolean isValid = scheduleConsumerService.validateExtraClass(extraClass);
        return ResponseEntity.ok(isValid);
    }
}

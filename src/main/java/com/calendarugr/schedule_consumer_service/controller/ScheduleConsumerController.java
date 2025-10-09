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
import com.calendarugr.schedule_consumer_service.dtos.TeacherClassesDTO;
import com.calendarugr.schedule_consumer_service.dtos.ErrorResponseDTO;
import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.calendarugr.schedule_consumer_service.services.ScheduleConsumerService;

@RestController
@RequestMapping("/schedule-consumer")
public class ScheduleConsumerController {

    @Autowired
    private ScheduleConsumerService scheduleConsumerService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ScheduleConsumerController.class);

    @GetMapping("/check")
    public ResponseEntity<?> checkService() {
        logger.info("Checking schedule consumer service health");
        return ResponseEntity.ok("Schedule consumer service is running");
    }

    @GetMapping("/classes-from-group")
    public ResponseEntity<?> getClasses(@RequestParam String grade, @RequestParam String subject, @RequestParam String group) {
        List<ClassInfo> classes = scheduleConsumerService.getClasses(grade, subject, group);
        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("NotFound", "No se encontraron clases para el grupo " + group + " de la materia " + subject + " del grado " + grade));
        }
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/grades")
    public ResponseEntity<?> getGrades() {
        List<FieldGradeDTO> grades = scheduleConsumerService.getGrades();
        if (grades.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("NotFound", "No se encontraron grados"));
        }
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/subjects-groups")
    public ResponseEntity<?> getSubjectsAndGroups(@RequestParam String grade) {
        List<SubjectGroupsDTO> subjectsGroups = scheduleConsumerService.getSubjectsAndGroups(grade);
        if (subjectsGroups.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("NotFound", "No se encontraron materias y grupos para el grado " + grade));
        }
        return ResponseEntity.ok(subjectsGroups);
    }

    @GetMapping("/teacher-classes")
    public ResponseEntity<?> getTeacherClasses(@RequestParam String partialTeacherName){
        List<TeacherClassesDTO> classes = scheduleConsumerService.getTeacherClasses(partialTeacherName);
        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ErrorResponseDTO("NoContent", "No se encontraron clases para el usuario"));
        }
        return ResponseEntity.ok(classes);               
    }

    @PostMapping("/classes-per-subscriptions")
    public ResponseEntity<?> getClassesPerSubscriptions(@RequestBody List<SubscriptionDTO> subscriptions) {
        List<ClassDTO> classes = scheduleConsumerService.getClassesPerSubscriptions(subscriptions);
        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("NotFound", "No se encontraron clases para las suscripciones"));
        }
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/subscription-validation")
    public ResponseEntity<Boolean> validateSubscription(@RequestBody SubscriptionDTO subscription) {
        boolean isValid = scheduleConsumerService.validateSubscription(subscription);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/extraclass-validation")
    public ResponseEntity<Boolean> validateExtraClass(@RequestBody ExtraClassDTO extraClass) {
        boolean isValid = scheduleConsumerService.validateExtraClass(extraClass);
        return ResponseEntity.ok(isValid);
    }
}
package com.calendarugr.schedule_consumer_service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.calendarugr.schedule_consumer_service.dtos.ClassDTO;
import com.calendarugr.schedule_consumer_service.dtos.ExtraClassDTO;
import com.calendarugr.schedule_consumer_service.dtos.FieldGradeDTO;
import com.calendarugr.schedule_consumer_service.dtos.SubjectGroupsDTO;
import com.calendarugr.schedule_consumer_service.dtos.SubscriptionDTO;
import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.calendarugr.schedule_consumer_service.entities.Grade;
import com.calendarugr.schedule_consumer_service.entities.Group;
import com.calendarugr.schedule_consumer_service.entities.Subject;
import com.calendarugr.schedule_consumer_service.repositories.ClassInfoRepository;
import com.calendarugr.schedule_consumer_service.repositories.GradeRepository;
import com.calendarugr.schedule_consumer_service.repositories.GroupRepository;
import com.calendarugr.schedule_consumer_service.repositories.SubjectRepository;

@Service
public class ScheduleConsumerService {

    @Value("${server.port}")
    private String port;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired 
    private ClassInfoRepository classInfoRepository;

    //Logger
    private static final Logger logger = LoggerFactory.getLogger(ScheduleConsumerService.class);

    private Optional<Grade> findGradeByName(String name) {
        return gradeRepository.findByName(name);
    }

    private Optional<Subject> getSubjectByNameAndGrade (String name, String grade) {
        logger.info("Grade: " + grade);
        Optional<Grade> gradeEntity = findGradeByName(grade);
        if (gradeEntity.isPresent()) {
            return subjectRepository.findByNameAndGrade(name, gradeEntity.get());
        }
        return Optional.empty();
    }

    private Optional<Group> getGroupByNameAndSubjectName (String name, String subjectName, String grade) {
        Optional<Subject> subject = getSubjectByNameAndGrade(subjectName, grade);
        if (subject.isPresent()) {
            return groupRepository.findByNameAndSubject(name, subject.get());
        }
        return Optional.empty();
    }
    
    public List<ClassInfo> getClasses(String grade, String subject, String group) {

        // Empty list
        List<ClassInfo> classes = List.of();
        Optional<Group> groupOptional = getGroupByNameAndSubjectName(group, subject, grade);
        if (groupOptional.isPresent()) {
            Group groupEntity = groupOptional.get();
            classes = classInfoRepository.findBySubjectGroup(groupEntity);
            if (classes.isEmpty()) {
                logger.info("No classes found for group: " + group);
            } 
        }

        return classes;

    }

    public List<FieldGradeDTO> getGrades() {
        HashMap<String, List<Map<String, String>>> gradesMap = new HashMap<>();
        List<FieldGradeDTO> grades = new ArrayList<>();
        List<Grade> gradesEntity = gradeRepository.findAll();
    
        if (gradesEntity.isEmpty()) {
            return List.of();
        }
    
        for (Grade grade : gradesEntity) {
            if (!gradesMap.containsKey(grade.getField())) {
                gradesMap.put(grade.getField(), new ArrayList<>());
            }
            // Crear un mapa con las claves "grade" y "faculty"
            Map<String, String> gradeEntry = Map.of(
                "grade", grade.getName(),
                "faculty", grade.getFaculty()
            );
            gradesMap.get(grade.getField()).add(gradeEntry);
        }
    
        for (String field : gradesMap.keySet()) {
            FieldGradeDTO fieldGrade = new FieldGradeDTO(field, gradesMap.get(field));
            grades.add(fieldGrade);
        }
        return grades;
    }

    public List<SubjectGroupsDTO> getSubjectsAndGroups(String grade) {
        Optional<Grade> gradeEntity = findGradeByName(grade);
        if (gradeEntity.isPresent()) {
            List<Subject> subjects = subjectRepository.findByGrade(gradeEntity.get());

            if (!subjects.isEmpty()) {
                return subjects.stream().map(subject -> {
                    List<Group> groups = groupRepository.findBySubject(subject);
                    if (groups.isEmpty()) { // Sometimes the prev method returns an empty list if there is only one group
                        Group group = groupRepository.findFirstBySubject(subject.getId());
                        if (group != null) {
                            groups.add(group);
                        } else {
                            logger.info("No groups found for subject: " + subject.getName() + " with id " + subject.getId());
                            logger.info("SQL : SELECT * FROM subject_group WHERE subject = " + subject.getId() + " LIMIT 1");
                        }
                    }
                    return new SubjectGroupsDTO(subject.getName(), groups.stream().map(Group::getName).toList());
                }).toList();
            }
        }
        return List.of();
    }

    public List<ClassDTO> getClassesPerSubscriptions(List<SubscriptionDTO> subscriptions) {
        List<ClassInfo> classes = new ArrayList<>();

        if (subscriptions.isEmpty()) {
            return List.of();
        }

        for (SubscriptionDTO subscription : subscriptions) {
            // Use the getClasses method to get the classes of each subscription
            List<ClassInfo> classesSubscription = getClasses(subscription.getGrade(), subscription.getSubject(), subscription.getGroup());
            classes.addAll(classesSubscription); 
        }

        // From List<ClassInfo> to List<ClassDTO>
        List<ClassDTO> classesDTO = classes.stream().map(classInfo -> {
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassroom(classInfo.getClassroom());
            classDTO.setDay(classInfo.getDay());
            classDTO.setInitDate(classInfo.getInitDate());
            classDTO.setFinishDate(classInfo.getFinishDate());
            classDTO.setInitHour(classInfo.getInitHour());
            classDTO.setFinishHour(classInfo.getFinishHour());
            classDTO.setGroup(classInfo.getSubjectGroup().getName());
            classDTO.setSubject(classInfo.getSubjectGroup().getSubject().getName());
            classDTO.setTeachers(classInfo.getSubjectGroup().getTeacher());
            classDTO.setGrade(classInfo.getSubjectGroup().getSubject().getGrade().getName());
            classDTO.setSubjectUrl(classInfo.getSubjectGroup().getSubject().getUrl());
            return classDTO;
        }).toList();

        return classesDTO;
    }

    public boolean validateSubscription(SubscriptionDTO subscription) {
        // First we need to check if the grade exists
        Optional<Grade> grade = findGradeByName(subscription.getGrade());
        if (grade.isEmpty()) {
            return false;
        }

        // Then we need to check if the subject exists, and if so, if belongs to the grade
        Optional<Subject> subject = getSubjectByNameAndGrade(subscription.getSubject(), subscription.getGrade());
        if (subject.isEmpty()) {
            return false;
        }

        // Finally we need to check if the group exists, and if so, if belongs to the subject
        Optional<Group> group = getGroupByNameAndSubjectName(subscription.getGroup(), subscription.getSubject(), subscription.getGrade());
        if (group.isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean validateExtraClass(ExtraClassDTO extraClass) {

        if (extraClass.getType().equals("GROUP")) { // A group event depends on classroom
            // First we need to check if the grade exists
            Optional<Grade> grade = findGradeByName(extraClass.getGradeName());
            if (grade.isEmpty()) {
                return false;
            }

            // Then we need to check if the subject exists, and if so, if belongs to the grade
            Optional<Subject> subject = getSubjectByNameAndGrade(extraClass.getSubjectName(), extraClass.getGradeName());
            if (subject.isEmpty()) {
                return false;
            }

            // Finally we need to check if the group exists, and if so, if belongs to the subject
            Optional<Group> group = getGroupByNameAndSubjectName(extraClass.getGroupName(), extraClass.getSubjectName(), extraClass.getGradeName());
            if (group.isEmpty()) {
                return false;
            }

            // We need to check if there is conflict with classes in the same grade, subject, group, date, day, init hour and finish hour
            List<ClassInfo> conflicts = classInfoRepository.findConflictingClassesOnGroupEvent(
                extraClass.getFacultyName(),
                extraClass.getDay(),
                extraClass.getDate(),
                extraClass.getClassroom(),
                extraClass.getInitHour(),
                extraClass.getFinishHour()
            );

            if (!conflicts.isEmpty()) {
                return false;
            }

        }else{ // A faculty event doesn't depend on classroom 

            List<ClassInfo> conflicts = classInfoRepository.findConflictingClassesOnFacultyEvent(
                extraClass.getFacultyName(),
                extraClass.getDay(),
                extraClass.getDate(),
                extraClass.getInitHour(),
                extraClass.getFinishHour()
            );

            if (!conflicts.isEmpty()) {
                return false;
            }

        }

        return true;
        
    }

}

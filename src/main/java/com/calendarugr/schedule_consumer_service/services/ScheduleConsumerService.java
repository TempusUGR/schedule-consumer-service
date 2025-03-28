package com.calendarugr.schedule_consumer_service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.calendarugr.schedule_consumer_service.dtos.ClassDTO;
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

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired 
    private ClassInfoRepository classInfoRepository;

    private Optional<Grade> findGradeByName(String name) {
        return gradeRepository.findByName(name);
    }

    private Optional<Subject> getSubjectByNameAndGrade (String name, String grade) {
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
            System.out.println("Group: " + groupEntity.getName() + " ID " + groupEntity.getId());
            classes = classInfoRepository.findBySubjectGroup(groupEntity);
            if (classes.isEmpty()) {
                System.out.println("Classes empty");
            }
        }

        return classes;

    }

    public List<FieldGradeDTO> getGrades() {
        HashMap<String, List<String>> gradesMap = new HashMap<>();
        List<FieldGradeDTO> grades = new ArrayList<>();
        List<Grade> gradesEntity = gradeRepository.findAll();
        if (gradesEntity.isEmpty()) {
            return List.of();
        }
        
        for (Grade grade : gradesEntity) {
            if (!gradesMap.containsKey(grade.getField())) {
                gradesMap.put(grade.getField(), new ArrayList<>());
            }
            gradesMap.get(grade.getField()).add(grade.getName());
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

}

package com.calendarugr.schedule_consumer_service.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            }
        }
        return classes;

    }

}

package com.calendarugr.schedule_consumer_service.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.calendarugr.schedule_consumer_service.entities.Group;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Long> {

    List<ClassInfo> findByDayAndInitHourAndClassroom(String day, LocalTime localTime, String classroom);

    List<ClassInfo> findBySubjectGroup(Group subjectGroup);

    List<ClassInfo> findByDayAndInitHourAndClassroomAndSubjectGroup(String day, LocalTime localTime, String classroom, Group subjectGroup);

    List<ClassInfo> findByDayAndInitHourAndInitDateAndFinishDateAndClassroomAndSubjectGroup(String day, LocalTime localTime, LocalDate initDate, LocalDate finishDate,
            String classroom, Group subjectGroup);
}

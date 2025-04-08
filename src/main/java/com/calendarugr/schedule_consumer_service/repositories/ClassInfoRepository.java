package com.calendarugr.schedule_consumer_service.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        @Query("SELECT c FROM ClassInfo c " +
        "JOIN c.subjectGroup sg " +
        "JOIN sg.subject s " +
        "JOIN s.grade g " +
        "WHERE g.name = :facultyName " +
        "AND c.classroom = :classroom " +
        "AND :date BETWEEN c.initDate AND c.finishDate " +
        "AND (" +
        "  (c.initHour < :finishHour AND c.finishHour > :initHour) " +
        ")")
        List<ClassInfo> findConflictingClassesOnGroupEvent(@Param("facultyName") String facultyName,
                @Param("date") LocalDate date,
                @Param("classroom") String classroom,
                @Param("initHour") LocalTime initHour,
                @Param("finishHour") LocalTime finishHour);

        @Query("SELECT c FROM ClassInfo c " +
        "JOIN c.subjectGroup sg " +
        "JOIN sg.subject s " +
        "JOIN s.grade g " +
        "WHERE g.name = :facultyName " +
        "AND :date BETWEEN c.initDate AND c.finishDate " +
        "AND (" +
        "  (c.initHour < :finishHour AND c.finishHour > :initHour) " +
        ")")
        List<ClassInfo> findConflictingClassesOnFacultyEvent(@Param("facultyName") String facultyName,
                @Param("date") LocalDate date,
                @Param("initHour") LocalTime initHour,
                @Param("finishHour") LocalTime finishHour);
}

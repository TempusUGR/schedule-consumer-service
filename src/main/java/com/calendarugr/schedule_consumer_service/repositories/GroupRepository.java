package com.calendarugr.schedule_consumer_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.calendarugr.schedule_consumer_service.entities.Group;
import com.calendarugr.schedule_consumer_service.entities.Subject;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> { 
    Optional<Group> findByNameAndSubject(String name, Subject subject);
    List<Group> findBySubject(Subject subject);
    
    @Query(value = "SELECT * FROM subject_group WHERE subject = :subjectId LIMIT 1", nativeQuery = true)
    Group findFirstBySubject(@Param("subjectId") Long subjectId);
}

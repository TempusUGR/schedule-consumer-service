package com.calendarugr.schedule_consumer_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calendarugr.schedule_consumer_service.entities.Group;
import com.calendarugr.schedule_consumer_service.entities.Subject;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> { 
    Optional<Group> findByNameAndSubject(String name, Subject subject);
}

package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.Registration;
import com.college.eventmanagement.model.RegistrationStatus;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByStudent(User student);

    List<Registration> findByEvent(Event event);

    List<Registration> findByEventAndStatus(Event event, RegistrationStatus status);

    Optional<Registration> findByEventAndStudent(Event event, User student);

    Optional<Registration> findByQrToken(String qrToken);

    boolean existsByEventAndStudent(Event event, User student);

    long countByEventAndStatus(Event event, RegistrationStatus status);

    @Query("SELECT r FROM Registration r WHERE r.event.id = :eventId AND r.status = 'WAITLISTED' ORDER BY r.registeredAt ASC")
    List<Registration> findFirstWaitlistedAttendee(@Param("eventId") Long eventId);
}

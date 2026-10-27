package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCreatedBy(User user);

    List<Event> findByStatusAndCategory(EventStatus status, String category);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Event> searchApprovedEvents(@Param("status") EventStatus status, @Param("query") String query);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.venue.id = :venueId AND e.eventDate = :eventDate AND e.status = 'APPROVED'")
    long countVenueBookings(@Param("venueId") Long venueId, @Param("eventDate") LocalDate eventDate);
}

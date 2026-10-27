package com.college.eventmanagement;

import com.college.eventmanagement.dto.EventRequestDto;
import com.college.eventmanagement.dto.EventResponseDto;
import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@SuppressWarnings("null")
public class EventCreationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateEventAndRegisterWithoutConflict() {
        User organizer = userRepository.findByEmail("acm.chapter@campus.edu").orElseThrow();

        EventRequestDto dto = new EventRequestDto();
        dto.setTitle("Test MGM Hackathon 2026");
        dto.setCategory("Tech");
        dto.setDepartment("Computer Science");
        dto.setDescription("Testing event creation without primary key violation");
        dto.setEventDate(LocalDate.now().plusDays(20));
        dto.setEventTime("10:00 AM");
        dto.setMaxSeats(250);
        dto.setPriceType("Free");
        dto.setPrice(0.0);
        dto.setVenueId(1L);

        // This must succeed without primary key conflict
        EventResponseDto created = eventService.createEvent(dto, organizer);
        Assertions.assertNotNull(created.getId());
        Assertions.assertNotNull(created.getId(), "Event ID should not be null");

        // Fresh student registration test
        userRepository.findByEmail("test.student@mgm.edu").ifPresent(userRepository::delete);
        User freshStudent = User.builder()
                .name("Test Student")
                .email("test.student@mgm.edu")
                .password("student123")
                .role(Role.ROLE_STUDENT)
                .collegeId("CCV25CS999")
                .branch("Computer Science")
                .semester("2nd Year")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .build();
        User savedStudent = userRepository.save(freshStudent);
        Assertions.assertNotNull(savedStudent.getId(), "Student ID should not be null");
    }
}

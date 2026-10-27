package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.RegistrationRequestDto;
import com.college.eventmanagement.dto.RegistrationResponseDto;
import com.college.eventmanagement.model.*;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.RegistrationRepository;
import com.college.eventmanagement.service.NotificationService;
import com.college.eventmanagement.service.RegistrationService;
import com.college.eventmanagement.util.QRCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
                                   EventRepository eventRepository,
                                   NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public RegistrationResponseDto registerForEvent(RegistrationRequestDto dto, User student) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + dto.getEventId()));

        // Rule 1: Event must be Approved
        if (event.getStatus() != EventStatus.APPROVED) {
            throw new IllegalStateException("Cannot register for an event that is not approved.");
        }

        // Rule 2: Registration deadline check
        if (event.getRegistrationDeadline() != null && LocalDate.now().isAfter(event.getRegistrationDeadline())) {
            throw new IllegalStateException("Registration deadline for this event has passed.");
        }

        // Rule 3: No duplicate registration
        if (registrationRepository.existsByEventAndStudent(event, student)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        // Rule 4: Seat limit check & waitlist logic
        RegistrationStatus status = RegistrationStatus.CONFIRMED;
        if (event.getRegisteredCount() >= event.getMaxSeats()) {
            status = RegistrationStatus.WAITLISTED;
        } else {
            event.setRegisteredCount(event.getRegisteredCount() + 1);
            eventRepository.save(event);
        }

        String qrToken = "CP-EVT" + event.getId() + "-" + (student.getCollegeId() != null ? student.getCollegeId() : student.getId()) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Registration registration = Registration.builder()
                .event(event)
                .student(student)
                .ticketTier(dto.getTicketTier() != null ? dto.getTicketTier() : "Standard Pass")
                .pricePaid(dto.getPricePaid() != null ? dto.getPricePaid() : 0.0)
                .status(status)
                .qrToken(qrToken)
                .checkedIn(false)
                .registeredAt(LocalDateTime.now())
                .build();

        Registration saved = registrationRepository.save(registration);

        // Send notification
        String msg = status == RegistrationStatus.CONFIRMED
                ? "Your registration for '" + event.getTitle() + "' is confirmed! QR pass generated."
                : "The event '" + event.getTitle() + "' is full. You have been placed on the Waitlist.";
        notificationService.sendNotification(student, msg, "REGISTRATION_" + status.name());

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long eventId, User student) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        Registration reg = registrationRepository.findByEventAndStudent(event, student)
                .orElseThrow(() -> new IllegalArgumentException("No registration found for this event."));

        boolean wasConfirmed = (reg.getStatus() == RegistrationStatus.CONFIRMED);
        registrationRepository.delete(reg);

        if (wasConfirmed) {
            if (event.getRegisteredCount() > 0) {
                event.setRegisteredCount(event.getRegisteredCount() - 1);
            }

            // Automatic waitlist promotion logic
            List<Registration> waitlist = registrationRepository.findFirstWaitlistedAttendee(event.getId());
            if (!waitlist.isEmpty()) {
                Registration promoted = waitlist.get(0);
                promoted.setStatus(RegistrationStatus.CONFIRMED);
                registrationRepository.save(promoted);

                event.setRegisteredCount(event.getRegisteredCount() + 1);

                // Notify promoted student
                notificationService.sendNotification(
                        promoted.getStudent(),
                        "Great news! A seat opened up and your waitlist pass for '" + event.getTitle() + "' is now CONFIRMED!",
                        "WAITLIST_PROMOTED"
                );
            }

            eventRepository.save(event);
        }
    }

    @Override
    public List<RegistrationResponseDto> getMyRegistrations(User student) {
        return registrationRepository.findByStudent(student)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<RegistrationResponseDto> getEventParticipants(Long eventId, User requester) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        if (requester.getRole() != Role.ROLE_ADMIN && requester.getRole() != Role.ROLE_DEAN &&
            (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(requester.getId()))) {
            throw new IllegalArgumentException("Unauthorized to view participant roster.");
        }

        return registrationRepository.findByEvent(event)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RegistrationResponseDto verifyAndCheckInAttendee(String qrToken) {
        Registration reg = registrationRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid QR pass token. No record found."));

        reg.setCheckedIn(true);
        Registration saved = registrationRepository.save(reg);
        return mapToDto(saved);
    }

    private RegistrationResponseDto mapToDto(Registration reg) {
        String qrBase64 = "";
        try {
            qrBase64 = QRCodeUtil.generateQRCodeBase64(reg.getQrToken(), 200, 200);
        } catch (Exception e) {
            qrBase64 = "";
        }

        return RegistrationResponseDto.builder()
                .id(reg.getId())
                .eventId(reg.getEvent().getId())
                .eventTitle(reg.getEvent().getTitle())
                .studentName(reg.getStudent().getName())
                .rollNo(reg.getStudent().getCollegeId())
                .email(reg.getStudent().getEmail())
                .ticketTier(reg.getTicketTier())
                .pricePaid(reg.getPricePaid())
                .status(reg.getStatus())
                .qrToken(reg.getQrToken())
                .qrCodeBase64(qrBase64)
                .checkedIn(reg.isCheckedIn())
                .registeredAt(reg.getRegisteredAt())
                .build();
    }
}

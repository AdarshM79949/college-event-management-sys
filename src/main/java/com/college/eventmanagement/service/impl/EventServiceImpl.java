package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.EventRequestDto;
import com.college.eventmanagement.dto.EventResponseDto;
import com.college.eventmanagement.model.*;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.VenueRepository;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final NotificationService notificationService;

    public EventServiceImpl(EventRepository eventRepository,
                            VenueRepository venueRepository,
                            NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto dto, User creator) {
        Venue venue = null;
        if (dto.getVenueId() != null) {
            venue = venueRepository.findById(dto.getVenueId()).orElse(null);
            
            // Check for venue booking conflict on that date
            if (venue != null) {
                long conflicts = eventRepository.countVenueBookings(venue.getId(), dto.getEventDate());
                if (conflicts > 0) {
                    throw new IllegalArgumentException("Venue '" + venue.getName() + "' is already booked for " + dto.getEventDate());
                }
            }
        }

        Event event = Event.builder()
                .title(dto.getTitle())
                .tagline(dto.getTagline())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .department(dto.getDepartment())
                .eventDate(dto.getEventDate())
                .eventTime(dto.getEventTime())
                .venue(venue)
                .bannerPath(dto.getBannerPath() != null ? dto.getBannerPath() : "assets/images/tech_hackathon.jpg")
                .registrationDeadline(dto.getRegistrationDeadline())
                .maxSeats(dto.getMaxSeats() != null ? dto.getMaxSeats() : 100)
                .registeredCount(0)
                .priceType(dto.getPriceType() != null ? dto.getPriceType() : "Free")
                .price(dto.getPrice() != null ? dto.getPrice() : 0.0)
                .status(EventStatus.PENDING) // Always starts as Pending per Build Guide
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();

        Event saved = eventRepository.save(event);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getApprovedEvents() {
        return eventRepository.findByStatus(EventStatus.APPROVED)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getPendingEvents() {
        return eventRepository.findByStatus(EventStatus.PENDING)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByOrganizer(User organizer) {
        return eventRepository.findByCreatedBy(organizer)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> searchEvents(String query, String category) {
        List<Event> events;
        if (query != null && !query.trim().isEmpty()) {
            events = eventRepository.searchApprovedEvents(EventStatus.APPROVED, query);
        } else if (category != null && !category.equalsIgnoreCase("all")) {
            events = eventRepository.findByStatusAndCategory(EventStatus.APPROVED, category);
        } else {
            events = eventRepository.findByStatus(EventStatus.APPROVED);
        }
        return events.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventDtoById(Long id) {
        return mapToDto(getEventEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventEntityById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));
    }

    @Override
    @Transactional
    public EventResponseDto updateEventStatus(Long eventId, EventStatus status, String comment) {
        Event event = getEventEntityById(eventId);
        event.setStatus(status);
        event.setAdminComment(comment);
        Event updated = eventRepository.save(event);

        if (event.getCreatedBy() != null) {
            String msg = "Your event proposal '" + event.getTitle() + "' has been " + status.name() + 
                         (comment != null ? ". Admin note: " + comment : ".");
            notificationService.sendNotification(event.getCreatedBy(), msg, "EVENT_" + status.name());
        }

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void cancelEvent(Long eventId, User requester) {
        Event event = getEventEntityById(eventId);
        if (requester.getRole() != Role.ROLE_ADMIN && requester.getRole() != Role.ROLE_DEAN && 
            (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(requester.getId()))) {
            throw new IllegalArgumentException("Only the event creator, an Admin, or the Dean can cancel this event.");
        }
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    private EventResponseDto mapToDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .tagline(event.getTagline())
                .description(event.getDescription())
                .category(event.getCategory())
                .department(event.getDepartment())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .venueId(event.getVenue() != null ? event.getVenue().getId() : null)
                .venueName(event.getVenue() != null ? event.getVenue().getName() : "Campus Main Ground")
                .bannerPath(event.getBannerPath())
                .registrationDeadline(event.getRegistrationDeadline())
                .maxSeats(event.getMaxSeats())
                .registeredCount(event.getRegisteredCount())
                .priceType(event.getPriceType())
                .price(event.getPrice())
                .status(event.getStatus())
                .adminComment(event.getAdminComment())
                .organizerName(event.getCreatedBy() != null ? event.getCreatedBy().getName() : "Student Council")
                .build();
    }
}

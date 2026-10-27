package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.EventRequestDto;
import com.college.eventmanagement.dto.EventResponseDto;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.model.User;

import java.util.List;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto dto, User creator);
    List<EventResponseDto> getApprovedEvents();
    List<EventResponseDto> getPendingEvents();
    List<EventResponseDto> getEventsByOrganizer(User organizer);
    List<EventResponseDto> searchEvents(String query, String category);
    EventResponseDto getEventDtoById(Long id);
    Event getEventEntityById(Long id);
    EventResponseDto updateEventStatus(Long eventId, EventStatus status, String comment);
    void cancelEvent(Long eventId, User requester);
}

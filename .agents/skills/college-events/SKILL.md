---
name: college-events
description: >-
  Domain knowledge and operational guidelines for building and extending College Event Management Systems (CampusPulse), including event lifecycles, ticketing, role management, venue booking, and QR verification.
---

# College Event Management System Skill

## Core Domain Entities
- **Event**: id, title, description, category (Tech, Cultural, Sports, Academic, Workshop, Gaming), department, bannerUrl, date, time, venue, capacity, registeredCount, priceType (Free / Paid), ticketTiers, schedule, status (Pending, Approved, Live, Completed, Cancelled).
- **Registration**: id, eventId, studentName, rollNo, email, department, year, ticketTier, registrationDate, checkInStatus, qrHash.
- **Venue**: id, name, type (Auditorium, Seminar Hall, Lab, Ground, Theater), capacity, bookedSlots.
- **Role**: `student` | `organizer` | `admin`.

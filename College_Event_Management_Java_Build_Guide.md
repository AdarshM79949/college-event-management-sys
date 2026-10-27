# College Event Management System – Java Build Guide

A step-by-step documentation plan for building the project in **Java with MySQL**, based on the abstract and reference paper. No code is included; this is a blueprint to follow while building.

---

## 1. Project Goal

Build a web application where:
- **Admins** approve events and manage users.
- **Organizers (clubs)** create and manage events.
- **Students** browse approved events and register.

---

## 2. Recommended Tech Stack

| Layer | Choice | Why |
|-------|--------|-----|
| Language | Java 17 or 21 (LTS) | Matches the abstract |
| Framework | Spring Boot | Fast setup, industry standard |
| Security | Spring Security | Login, roles, password hashing |
| Data access | Spring Data JPA (Hibernate) | Less boilerplate for database work |
| Database | MySQL 8 | Named in the abstract |
| Frontend | Thymeleaf + HTML/CSS/JS (simple) or React (advanced) | Thymeleaf is easiest for a first version |
| Build tool | Maven | Simple dependency management |
| Email | Spring Mail (SMTP) | Notifications |
| QR codes | ZXing library | Check-in QR codes (optional) |
| Version control | Git + GitHub | Team collaboration |
| IDE | IntelliJ IDEA or VS Code | Either works |

---

## 3. Tools to Install

1. JDK 17 or 21
2. Maven
3. MySQL Server and MySQL Workbench
4. IntelliJ IDEA (Community) or VS Code with Java extensions
5. Git
6. Postman (for testing APIs)

---

## 4. Roles and Permissions

| Role | Can do |
|------|--------|
| Admin | Approve/reject events, manage users and roles, view reports |
| Organizer | Create, edit, cancel own events; upload banners; view participants |
| Student | Register/login, browse approved events, register or cancel, view history |

---

## 5. Database Design

### Tables

**users**
- id, name, email, password (hashed), role, college_id, branch, semester, is_verified, created_at

**clubs** (optional, if organizers belong to clubs)
- id, club_name, organizer_user_id

**events**
- id, title, description, category (cultural, academic, sports), event_date, event_time, venue, banner_path, registration_deadline, max_seats, status (Pending, Approved, Rejected, Cancelled), admin_comment, created_by, created_at

**registrations**
- id, event_id, student_id, status (Confirmed, Waitlisted, Cancelled), qr_token, checked_in, registered_at

**notifications**
- id, user_id, message, type, is_read, created_at

**feedback**
- id, event_id, student_id, rating, comment, created_at

### Relationships
- One user (organizer) creates many events.
- One event has many registrations.
- One student has many registrations.
- A student can register for an event only once (unique pair of event and student).
- One user has many notifications.

---

## 6. Project Structure (Layered)

```
src/main/java/.../eventmanagement
├── config          (security and app configuration)
├── controller      (web and REST endpoints)
├── service         (business logic)
├── repository      (database access)
├── model           (entities)
├── dto             (request/response objects)
├── exception       (custom errors and handlers)
└── util            (helpers: QR, file upload, email)

src/main/resources
├── templates       (Thymeleaf pages)
├── static          (css, js, images)
└── application.properties
```

Rule of thumb: controllers talk to services, services talk to repositories. Controllers never touch the database directly.

---

## 7. Build Phases

### Phase 1 – Setup (Day 1–2)
- Create the Spring Boot project with dependencies: Web, Security, JPA, MySQL Driver, Thymeleaf, Validation, Mail.
- Create the MySQL database and connect it in application properties.
- Push the empty project to GitHub and agree on a branching rule (one branch per feature).

### Phase 2 – User Management (Week 1)
- Create the user entity and repository.
- Build registration and login pages.
- Hash passwords with BCrypt.
- Configure role-based access: pages and actions restricted by Admin, Organizer, or Student.
- Add email verification (can be left for later).

### Phase 3 – Event Management (Week 2)
- Organizer form: title, description, date, time, venue, category, seats, deadline, banner upload.
- Organizer dashboard listing own events with Details, Update, Cancel.
- New events start with status Pending.

### Phase 4 – Event Approval (Week 2–3)
- Admin page listing Pending events.
- Approve or reject with a comment.
- Status changes to Approved or Rejected.
- Only Approved events appear to students.

### Phase 5 – Registration (Week 3)
- Student event list with search and category filter.
- Register button with checks: deadline not passed, event approved, no duplicate registration.
- Seat limit logic: if full, add to waitlist.
- Cancel participation; promote the first waitlisted student automatically.
- "My Events" page for the student.
- Organizer view of registered participants.

### Phase 6 – Notifications (Week 4)
- Trigger notifications on: event approved/rejected, registration confirmed, waitlist promotion, event cancelled.
- Deliver as email and as in-app notifications (bell icon list).

### Phase 7 – Extras (Week 4–5)
- QR code on confirmation, with a check-in page for organizers.
- Feedback form after the event ends.
- Admin reports: events per category, registrations per event, attendance.

### Phase 8 – Testing and Polish (Week 5–6)
- Test every flow for each role.
- Fix validation and error messages.
- Make pages responsive.
- Prepare the demo data and final presentation.

---

## 8. Page and Endpoint Plan

### Pages
| Role | Pages |
|------|-------|
| Public | Landing, Login, Register |
| Student | Dashboard, Events list, Event details, My Events |
| Organizer | Dashboard, Create event, Edit event, Event participants |
| Admin | Dashboard, Pending approvals, User management, Reports |

### REST Endpoints (if building an API-based version)
| Purpose | Method and path |
|---------|----------------|
| Register user | POST /api/auth/register |
| Login | POST /api/auth/login |
| List approved events | GET /api/events |
| Event details | GET /api/events/{id} |
| Create event | POST /api/events |
| Update event | PUT /api/events/{id} |
| Cancel event | DELETE /api/events/{id} |
| Approve or reject | PATCH /api/admin/events/{id}/status |
| Register for event | POST /api/events/{id}/register |
| Cancel registration | DELETE /api/events/{id}/register |
| My registrations | GET /api/students/me/registrations |
| Participants of an event | GET /api/events/{id}/participants |
| Notifications | GET /api/notifications |

---

## 9. Business Rules to Implement

1. A student cannot register twice for the same event.
2. Registration closes after the deadline.
3. Only Approved events can be registered for.
4. When seats are full, new registrations go to the waitlist.
5. When a confirmed student cancels, the first waitlisted student is promoted and notified.
6. Only the organizer who created an event can edit or cancel it.
7. Only admins can approve or reject events.
8. Passwords are never stored in plain text.

---

## 10. Security Checklist

- Hash passwords with BCrypt.
- Restrict URLs by role.
- Validate all form input on the server.
- Limit upload file type and size for banners.
- Protect against SQL injection by using JPA (no manual query strings from user input).
- Keep database credentials and email passwords outside source control.
- Use HTTPS when deployed.

---

## 11. Testing Plan

| Type | What to check |
|------|--------------|
| Unit tests | Service logic: seat limits, waitlist promotion, duplicate checks |
| Integration tests | Repository queries against a test database |
| Manual flow tests | Student: register → browse → register → cancel; Organizer: create → wait for approval; Admin: approve/reject |
| Security tests | Student cannot open admin pages; organizer cannot edit others' events |
| Edge cases | Last seat taken by two students at once, past deadline, empty lists |

---

## 12. Deployment Options

- **Local demo:** run on a laptop with local MySQL (fine for review presentations).
- **Cloud:** host the app on AWS, Render, or Railway; use a managed MySQL database.
- Package the app as a single runnable JAR for easy deployment.

---

## 13. Team Work Split (4 members)

| Member | Focus |
|--------|-------|
| Member 1 | Project setup, database design, user management and security |
| Member 2 | Event management and organizer dashboard |
| Member 3 | Registration, waitlist, and student pages |
| Member 4 | Approval module, notifications, reports, and testing |

Rotate reviewing each other's work through GitHub pull requests.

---

## 14. Milestones for Reviews

| Review | Deliverable |
|--------|------------|
| Zeroth | Abstract, objectives, scope (done) |
| First | Database design, UI mockups, login and registration working |
| Second | Event creation, approval, and student registration working |
| Final | Notifications, QR check-in, reports, testing, full demo |

---

## 15. Improvements Over the Reference Paper

- Fix its inconsistency (diagram shows PHP while text says Java) by documenting a single clear stack.
- Add the waitlist and QR check-in properly, which the paper only mentions.
- Add feedback and reports, so the system closes the loop after an event.
- Use secure password handling and role-based access from the start.
- Later: AI/ML features that fit your branch, such as event recommendations for students based on past participation, or attendance prediction to help plan venues.

---

## 16. Common Mistakes to Avoid

- Putting business logic in controllers.
- Storing uploaded images inside the database instead of the file system with a saved path.
- Skipping validation and trusting the form.
- Building every feature at once instead of finishing one phase before the next.
- Not committing to Git regularly.

---

*Note: your message ended with "and", so I assumed Java + MySQL as in your abstract. If you meant another pairing (for example Java + React, or Java + PostgreSQL), tell me and I will adjust the guide.*

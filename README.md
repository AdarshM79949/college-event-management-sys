# 🎓 CampusPulse: MGM College of Engineering
### Pure Java College Event Management System (Desktop Application)

Enterprise-grade College Event Management System built as a **Pure Java Desktop Application (Java 21 LTS + Swing GUI + FlatDarkLaf + Spring Data JPA + H2/MySQL + ZXing QR)**.

---

## 🌟 Core System Roles & Features

### 1. 👨‍🎓 Student Portal
- **Fresh Student Registration**: Students register freshly with their **MGM College Roll Number** (e.g. `CCV25AIM001`, `CCV25CS001`, `CCV25CC001`).
- **Flexible Login**: Log in directly using either **College Roll Number** or **Email**.
- **Event Discovery & Filtering**: Real-time keyword search and category filtering (*Tech, Cultural, Sports, Academic, Workshop, Gaming*).
- **Instant RSVP & QR Badge Generation**: 1-click event registration with live seat tracking and cryptographic QR token generation.
- **My Passes & Visual QR Badge**: View entry passes and click **"View QR Entry Badge"** to render a visual QR code for gate admission.
- **Cancel Registration**: Instant cancellation with automatic waitlist promotion.

### 2. 🎪 Organizer Studio
- **Event Proposal Wizard**: Propose campus events with target capacity, date, venue, category, and department (starts as `PENDING` awaiting Dean approval).
- **Hosted Events Tracker**: Track all hosted events, approval statuses, and attendee fill rates.
- **Gate QR Scanner / Token Check-In**: Instant token verification and check-in confirmation for students entering events.

### 3. 👑 Administrator & Dean Hub
- **Approval Workflow**: Review pending club submissions with 1-click **Approve** or **Reject** with Dean remarks.
- **Event Participants & Registrations**: Select any event to view the live participant roster with student names, MGM roll numbers, departments, and check-in statuses.
- **Student & Faculty Records**: View all registered users, roles, and roll numbers across the college.
- **Campus Metrics**: Live statistics on approved events, pending proposals, and total RSVPs.

---

## 🔑 Pre-Seeded Accounts (No Mock Students)

| Role | Email | Password | Details |
|---|---|---|---|
| **Dean of Student Affairs** | `dean@campus.edu` | `admin123` | Approves / rejects event proposals |
| **Club Organizer** | `acm.chapter@campus.edu` | `org123` | Proposes events and verifies QR gate tokens |
| **Fresh Students** | *(Register Freshly)* | `student123` | Use Roll Numbers: `CCV25CS001` (CS), `CCV25AIM001` (AIML), `CCV25CC001` (Cyber Security) |

---

## 🚀 Quick Setup & Run (Windows)

### 1. First-Time Setup
Double-click `setup.bat` or run in terminal:
```cmd
setup.bat
```
> **What `setup.bat` does:**
> - Automatically detects or downloads the portable Java 21 LTS runtime and Apache Maven if not present on your system.
> - Downloads all Spring Boot and UI framework dependencies.
> - Prepares the application for immediate execution without manual tool installation.

### 2. Launch CampusPulse
Double-click `run.bat` or run:
```cmd
run.bat
```
*(Or in PowerShell: `.\run.ps1`)*


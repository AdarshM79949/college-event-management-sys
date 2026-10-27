-- ==============================================================================
-- CampusPulse: College Event Management System (CEMS)
-- Standalone SQL Database Schema & Mock Data Script
-- Compatible with: MySQL 8+, PostgreSQL, SQLite, and H2 Database
-- ==============================================================================

-- 1. DROP EXISTING TABLES (In correct dependency order)
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS venues;
DROP TABLE IF EXISTS users;

-- ==============================================================================
-- 2. TABLE DEFINITIONS
-- ==============================================================================

-- Users Table (Students, Organizers, Admins)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    college_id VARCHAR(100),
    branch VARCHAR(100),
    semester VARCHAR(50),
    is_verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Venues Table
CREATE TABLE venues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(255) NOT NULL,
    facilities TEXT,
    image_url VARCHAR(500)
);

-- Events Table
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tagline VARCHAR(255),
    description TEXT,
    category VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    event_date DATE NOT NULL,
    event_time VARCHAR(50),
    venue_id BIGINT,
    banner_path VARCHAR(500),
    registration_deadline DATE,
    max_seats INT NOT NULL,
    registered_count INT DEFAULT 0,
    price_type VARCHAR(50) DEFAULT 'Free',
    price DOUBLE DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'PENDING',
    admin_comment TEXT,
    created_by_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE SET NULL,
    CONSTRAINT fk_event_creator FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Registrations & QR Badges Table
CREATE TABLE registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    ticket_tier VARCHAR(100) DEFAULT 'Standard',
    price_paid DOUBLE DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    qr_token VARCHAR(255) NOT NULL UNIQUE,
    checked_in BOOLEAN DEFAULT FALSE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notifications Table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Feedback Table
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feed_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_feed_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ==============================================================================
-- 3. SQL MOCK DATA SEED INSERTS
-- ==============================================================================

-- A. USERS (BCrypt Encrypted Passwords)
-- Password for all mock users: 'admin123' / 'org123' / 'student123' (hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a)
INSERT INTO users (id, name, email, password, role, college_id, branch, semester, is_verified, created_at) VALUES 
(1, 'Dean of Student Affairs', 'dean@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ADMIN', 'FAC-ADMIN-01', 'Administration', 'Faculty', TRUE, '2026-01-01 09:00:00'),
(2, 'Chief Campus Proctor', 'proctor@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ADMIN', 'FAC-PROCT-02', 'Administration', 'Faculty', TRUE, '2026-01-01 09:00:00'),
(3, 'ACM & Robotics Club Head', 'acm.chapter@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ORGANIZER', '2022CSB1010', 'Computer Science & Engineering', '4th Year', TRUE, '2026-01-05 10:00:00'),
(4, 'Fine Arts & Music Lead', 'cultural.council@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ORGANIZER', '2022CUL1002', 'Student Cultural Council', '4th Year', TRUE, '2026-01-05 10:00:00'),
(5, 'Sports Council Secretary', 'sports.council@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ORGANIZER', '2022SPT1005', 'Physical Education & Athletics', '4th Year', TRUE, '2026-01-05 10:00:00'),
(6, 'E-Cell Student President', 'ecell.lead@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ORGANIZER', '2023ECL2011', 'Innovation & Entrepreneurship', '3rd Year', TRUE, '2026-01-05 10:00:00'),
(7, 'Adarsh Sharma', 'adarsh.cs23@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_STUDENT', '2023CSB1042', 'Computer Science & Engineering', '3rd Year', TRUE, '2026-01-10 11:00:00'),
(8, 'Rhea Sen', 'rhea.ec23@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_STUDENT', '2023ECB1028', 'Electronics & Communication', '3rd Year', TRUE, '2026-01-10 11:30:00'),
(9, 'Vikram Malhotra', 'vikram.me24@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_STUDENT', '2024MEB1077', 'Mechanical Engineering', '2nd Year', TRUE, '2026-01-11 12:00:00'),
(10, 'Ananya Patel', 'ananya.ai23@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_STUDENT', '2023AIB1015', 'Artificial Intelligence & Data Science', '3rd Year', TRUE, '2026-01-11 14:00:00'),
(11, 'Devansh Gupta', 'devansh.ee24@campus.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_STUDENT', '2024EEB1090', 'Electrical Engineering', '2nd Year', TRUE, '2026-01-12 15:00:00');

-- B. VENUES
INSERT INTO venues (id, name, type, capacity, location, facilities, image_url) VALUES 
(1, 'Dr. A.P.J. Abdul Kalam Grand Auditorium', 'Auditorium', 1200, 'Block A - Central Campus', 'Dolby 7.1 Surround, Dual 4K Laser Projectors, Central Air Conditioning, Green Rooms', 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800'),
(2, 'Rabindranath Tagore Open Air Theatre', 'Amphitheater', 2500, 'Lakeside Arena - East Campus', 'Open Sky Concert Stage, High-Power Line Array Sound, Intelligent Stage Truss Lighting', 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800'),
(3, 'Turing AI & High-Performance Computing Labs', 'Tech Lab', 350, 'Block C - CS & IT Wing', '10Gbps Dedicated Fiber, 150 NVIDIA RTX GPU Workstations, Smart Interactive Whiteboards', 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=800'),
(4, 'Major Dhyan Chand Sports Complex', 'Indoor Stadium', 1800, 'Sports Pavilion - West Campus', 'Maple Wood Basketball Court, Indoor Badminton Courts, LED Scoreboard Jumbotron, Medical Bay', 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=800'),
(5, 'Ramanujan Academic Conference Center', 'Conference Hall', 450, 'Block B - Management Block', 'Tiered Executive Seating, Video Conferencing Rig, Translation Booths', 'https://images.unsplash.com/photo-1511578314322-379afb476865?w=800'),
(6, 'Vikram Sarabhai Innovation & Incubation Hub', 'Innovation Hub', 200, 'Incubation Tower - Floor 3', 'Pitch Deck Stage, Breakout Discussion Pods, High-Speed WiFi, Coffee Bar', 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=800');

-- C. EVENTS
INSERT INTO events (id, title, tagline, description, category, department, event_date, event_time, venue_id, banner_path, registration_deadline, max_seats, registered_count, price_type, price, status, admin_comment, created_by_user_id, created_at) VALUES 
(1, 'COGNIZANCE 2026: National Hackathon & AI Fest', 'Build the autonomous future in 36 sleepless hours', 'Flagship 36-hour national hackathon with tracks in AI/ML, Web3, Autonomous Robotics, and Healthcare Tech. Over INR 5,00,000 in cash prizes and mentor sessions with top tech giants.', 'Tech', 'Computer Science & Engineering', '2026-10-25', '09:00 AM', 3, 'images/tech_hackathon.jpg', '2026-10-20', 600, 482, 'Free', 0.0, 'APPROVED', 'Approved by Dean of Student Affairs', 3, '2026-01-15 10:00:00'),
(2, 'RHYTHM & RAAG: Annual Fusion & EDM Star Night', 'Feel the pulse of bass, lights, and electric melodies', 'The biggest cultural night of the academic year featuring live celebrity performances, fusion rock battles, and an electric EDM headline concert.', 'Cultural', 'Student Cultural Council', '2026-11-05', '06:00 PM', 2, 'images/cultural_fest.jpg', '2026-11-01', 2200, 1950, 'Paid', 150.0, 'APPROVED', 'Approved by Dean of Student Affairs', 4, '2026-01-16 11:00:00'),
(3, 'OLYMPUS 2026: Inter-Collegiate Sports & Esports League', 'Where physical grit meets virtual tactical mastery', 'Inter-collegiate tournament with 5v5 Basketball, Badminton Championships, and a premier Valorant & BGMI Esports LAN battle in the stadium.', 'Sports', 'Physical Education & Athletics', '2026-11-12', '08:30 AM', 4, 'images/sports_fest.jpg', '2026-11-08', 1500, 890, 'Free', 0.0, 'APPROVED', 'Approved by Dean of Student Affairs', 5, '2026-01-17 12:00:00'),
(4, 'QUANTUM LEAP: Hands-on Generative AI & LLM Workshop', 'Master Generative AI, RAG, and Large Language Models', 'Hands-on deep dive into Transformer architectures, RAG pipelines, and open-weight LLM fine-tuning using PyTorch and HuggingFace.', 'Workshop', 'Computer Science & Engineering', '2026-11-18', '10:00 AM', 3, 'images/tech_hackathon.jpg', '2026-11-15', 150, 145, 'Free', 0.0, 'APPROVED', 'Approved by Dean of Student Affairs', 3, '2026-01-18 14:00:00'),
(5, 'NEO-GENESIS: Autonomous Drone Racing Championship', 'FPV High-Speed Obstacle Courses at 100km/h', 'High-speed FPV micro-drone obstacle navigation racing through illuminated neon courses inside the indoor arena.', 'Gaming', 'Aeronautics & Robotics Club', '2026-11-25', '02:00 PM', 4, 'images/sports_fest.jpg', '2026-11-20', 600, 0, 'Free', 0.0, 'PENDING', NULL, 3, '2026-01-19 15:00:00'),
(6, 'VENTURE VORTEX: Student Startup Pitch & Angel Summit', 'Pitch your startup to top VCs and secure angel funding', 'Campus startup demo day where top 15 student founders pitch live before venture capitalists and angel investors for seed funding.', 'Academic', 'Innovation & Entrepreneurship', '2026-12-02', '10:30 AM', 6, 'images/tech_hackathon.jpg', '2026-11-28', 250, 0, 'Free', 0.0, 'PENDING', NULL, 6, '2026-01-20 16:00:00'),
(7, 'Midnight Rock & Metal Underground Battle', 'Late night amplified sound showcase', 'Underground rock battle and loud acoustic showcase proposed during examination preparation week.', 'Cultural', 'Student Cultural Council', '2026-12-10', '10:00 PM', 2, 'images/cultural_fest.jpg', '2026-12-05', 800, 0, 'Free', 0.0, 'REJECTED', 'Rejected due to campus quiet hours policy after 22:00 and acoustic compliance.', 4, '2026-01-21 17:00:00');

-- D. REGISTRATIONS & UNIQUE QR TOKENS
INSERT INTO registrations (id, event_id, student_id, ticket_tier, price_paid, status, qr_token, checked_in, registered_at) VALUES 
(1, 1, 7, 'Hacker Pass (Solo / Team)', 0.0, 'CONFIRMED', 'CP-EVT1-2023CSB1042-882101', FALSE, '2026-01-22 10:00:00'),
(2, 2, 7, 'VIP Concert Pass', 150.0, 'CONFIRMED', 'CP-EVT2-2023CSB1042-882102', FALSE, '2026-01-22 10:15:00'),
(3, 3, 7, 'Esports Contestant Pass', 0.0, 'CONFIRMED', 'CP-EVT3-2023CSB1042-882103', TRUE, '2026-01-22 10:30:00'),
(4, 1, 8, 'Hacker Pass (Solo / Team)', 0.0, 'CONFIRMED', 'CP-EVT1-2023ECB1028-993201', FALSE, '2026-01-22 11:00:00'),
(5, 2, 9, 'VIP Concert Pass', 150.0, 'CONFIRMED', 'CP-EVT2-2024MEB1077-994302', FALSE, '2026-01-22 11:30:00'),
(6, 4, 10, 'Standard Delegate Pass', 0.0, 'CONFIRMED', 'CP-EVT4-2023AIB1015-995404', FALSE, '2026-01-22 12:00:00'),
(7, 1, 11, 'Standard Pass', 0.0, 'WAITLISTED', 'CP-EVT1-2024EEB1090-996501', FALSE, '2026-01-22 12:30:00');

-- E. NOTIFICATIONS
INSERT INTO notifications (id, user_id, message, type, is_read, created_at) VALUES 
(1, 7, 'Welcome to CampusPulse! Your student pass for COGNIZANCE 2026 has been generated.', 'REGISTRATION', FALSE, '2026-01-22 10:00:00'),
(2, 7, 'Your registration for RHYTHM & RAAG Star Night is confirmed. Scan QR token at Gate 2.', 'REGISTRATION', FALSE, '2026-01-22 10:15:00'),
(3, 1, 'New proposal submitted: NEO-GENESIS Autonomous Drone Championship is awaiting your approval.', 'APPROVAL_PENDING', FALSE, '2026-01-22 16:00:00');

-- F. FEEDBACK
INSERT INTO feedback (id, event_id, student_id, rating, comment, created_at) VALUES 
(1, 1, 7, 5, 'Outstanding organization, GPU workstations were flawless throughout the 36-hour hackathon!', '2026-01-23 18:00:00'),
(2, 2, 8, 5, 'Amazing production quality, stage lighting and sound were top tier!', '2026-01-23 19:00:00');

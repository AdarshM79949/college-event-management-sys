-- ===================================================================
-- CampusPulse - MGM COLLEGE OF ENGINEERING
-- Baseline System Data: Admin, Dean, and Organizers (No Mock Data)
-- Students and Events are freshly entered through the application
-- ===================================================================

-- 1. PRE-SEEDED ADMINISTRATIVE & ORGANIZER ACCOUNTS
-- Password for Admins (dean, proctor): admin123
-- Password for Organizers (acm, cultural, sports, ecell): org123
MERGE INTO users (name, email, password, role, college_id, branch, semester, is_verified, created_at) KEY(email) VALUES 
('Dean of Student Affairs', 'dean@campus.edu', '$2a$10$.ahs04aw5pIFZl.Xk9CRFedRBZ/nLrQkap0a/3u3T1FSArY5HvqQq', 'ROLE_ADMIN', 'FAC-MGM-01', 'MGM College of Engineering - Administration', 'Faculty', TRUE, CURRENT_TIMESTAMP),
('Chief Campus Proctor', 'proctor@campus.edu', '$2a$10$.ahs04aw5pIFZl.Xk9CRFedRBZ/nLrQkap0a/3u3T1FSArY5HvqQq', 'ROLE_ADMIN', 'FAC-PROCT-02', 'MGM College of Engineering - Proctorial', 'Faculty', TRUE, CURRENT_TIMESTAMP),
('ACM & Coding Club Head', 'acm.chapter@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-ACM', 'Computer Science & Engineering', 'Lead / Faculty', TRUE, CURRENT_TIMESTAMP),
('Fine Arts & Music Lead', 'cultural.council@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-CUL', 'Student Cultural Council', 'Lead / Faculty', TRUE, CURRENT_TIMESTAMP),
('Sports Council Secretary', 'sports.council@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-SPT', 'Physical Education & Athletics', 'Lead / Faculty', TRUE, CURRENT_TIMESTAMP),
('E-Cell Student President', 'ecell.lead@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-ECL', 'Innovation & Entrepreneurship', 'Lead / Faculty', TRUE, CURRENT_TIMESTAMP),
('System Administrator', 'admin@campus.edu', '$2a$10$.ahs04aw5pIFZl.Xk9CRFedRBZ/nLrQkap0a/3u3T1FSArY5HvqQq', 'ROLE_ADMIN', 'FAC-ADM-00', 'MGM College of Engineering - Administration', 'Faculty', TRUE, CURRENT_TIMESTAMP);

-- 2. CAMPUS VENUES (MGM COLLEGE OF ENGINEERING)
MERGE INTO venues (id, name, type, capacity, location, facilities, image_url) KEY(id) VALUES 
(1, 'Dr. A.P.J. Abdul Kalam Grand Auditorium', 'Auditorium', 1200, 'MGM Campus - Central Auditorium Block', 'Dolby 7.1 Surround, Dual 4K Laser Projectors, Central AC, Green Rooms', 'images/auditorium.jpg'),
(2, 'MGM Open Air Amphitheatre', 'Amphitheater', 2500, 'Lakeside Arena - MGM East Quad', 'Concert Stage, High-Power Line Array Sound, Intelligent Stage Truss Lighting', 'images/amphitheatre.jpg'),
(3, 'Turing AI & High-Performance Computing Labs', 'Tech Lab', 350, 'MGM Block C - CS & AIML Wing', '10Gbps Dedicated Fiber, 150 NVIDIA RTX GPU Workstations, Smart Boards', 'images/tech_lab.jpg'),
(4, 'Major Dhyan Chand Sports Complex', 'Indoor Stadium', 1800, 'MGM Sports Pavilion - West Campus', 'Maple Wood Basketball Court, Badminton Courts, LED Scoreboard Jumbotron', 'images/sports_complex.jpg'),
(5, 'Ramanujan Academic Conference Center', 'Conference Hall', 450, 'MGM Block B - Seminar Hall', 'Tiered Executive Seating, Video Conferencing Rig, Translation Booths', 'images/conference_center.jpg'),
(6, 'Vikram Sarabhai Innovation & Incubation Hub', 'Innovation Hub', 200, 'MGM Incubation Tower - Floor 3', 'Pitch Deck Stage, Breakout Discussion Pods, High-Speed WiFi, Coffee Bar', 'images/incubation_hub.jpg');

-- 3. IDENTITY SEQUENCES AUTO-MANAGED BY H2 AND JPA
-- No hardcoded sequence reset to prevent primary key collision across application restarts


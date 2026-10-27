-- ===================================================================
-- CampusPulse - MGM COLLEGE OF ENGINEERING - MySQL Baseline Data
-- Only Admin, Dean, and Organizers (No Mock Students)
-- ===================================================================

USE `college_event_db`;

-- 1. USERS (ADMIN & ORGANIZERS ONLY)
INSERT INTO `users` (`id`, `name`, `email`, `password`, `role`, `college_id`, `branch`, `semester`, `is_verified`, `created_at`) VALUES 
(1, 'Dean of Student Affairs', 'dean@campus.edu', '$2a$10$.ahs04aw5pIFZl.Xk9CRFedRBZ/nLrQkap0a/3u3T1FSArY5HvqQq', 'ROLE_ADMIN', 'FAC-MGM-01', 'MGM College of Engineering - Administration', 'Faculty', TRUE, NOW()),
(2, 'Chief Campus Proctor', 'proctor@campus.edu', '$2a$10$.ahs04aw5pIFZl.Xk9CRFedRBZ/nLrQkap0a/3u3T1FSArY5HvqQq', 'ROLE_ADMIN', 'FAC-PROCT-02', 'MGM College of Engineering - Proctorial', 'Faculty', TRUE, NOW()),
(3, 'ACM & Coding Club Head', 'acm.chapter@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-ACM', 'Computer Science & Engineering', 'Faculty / Lead', TRUE, NOW()),
(4, 'Fine Arts & Music Lead', 'cultural.council@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-CUL', 'Student Cultural Council', 'Faculty / Lead', TRUE, NOW()),
(5, 'Sports Council Secretary', 'sports.council@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-SPT', 'Physical Education & Athletics', 'Faculty / Lead', TRUE, NOW()),
(6, 'E-Cell Student President', 'ecell.lead@campus.edu', '$2a$10$gU96gyfX7Scps5FPBWiBw.axuoQ.3n8I2H/oHhoUggzee.W6bhdJW', 'ROLE_ORGANIZER', 'CCV-ORG-ECL', 'Innovation & Entrepreneurship', 'Faculty / Lead', TRUE, NOW()),
(7, 'System Administrator', 'admin@campus.edu', '$2a$10$.ahs04aw5pIFZl.Xk9CRFedRBZ/nLrQkap0a/3u3T1FSArY5HvqQq', 'ROLE_ADMIN', 'FAC-ADM-00', 'MGM College of Engineering - Administration', 'Faculty', TRUE, NOW())
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 2. VENUES
INSERT INTO `venues` (`id`, `name`, `type`, `capacity`, `location`, `facilities`, `image_url`) VALUES 
(1, 'Dr. A.P.J. Abdul Kalam Grand Auditorium', 'Auditorium', 1200, 'MGM Campus - Central Auditorium Block', 'Dolby 7.1 Surround, Dual 4K Laser Projectors, Central AC, Green Rooms', 'images/auditorium.jpg'),
(2, 'MGM Open Air Amphitheatre', 'Amphitheater', 2500, 'Lakeside Arena - MGM East Quad', 'Concert Stage, High-Power Line Array Sound, Intelligent Stage Truss Lighting', 'images/amphitheatre.jpg'),
(3, 'Turing AI & High-Performance Computing Labs', 'Tech Lab', 350, 'MGM Block C - CS & AIML Wing', '10Gbps Dedicated Fiber, 150 NVIDIA RTX GPU Workstations, Smart Boards', 'images/tech_lab.jpg'),
(4, 'Major Dhyan Chand Sports Complex', 'Indoor Stadium', 1800, 'MGM Sports Pavilion - West Campus', 'Maple Wood Basketball Court, Badminton Courts, LED Scoreboard Jumbotron', 'images/sports_complex.jpg'),
(5, 'Ramanujan Academic Conference Center', 'Conference Hall', 450, 'MGM Block B - Seminar Hall', 'Tiered Executive Seating, Video Conferencing Rig, Translation Booths', 'images/conference_center.jpg'),
(6, 'Vikram Sarabhai Innovation & Incubation Hub', 'Innovation Hub', 200, 'MGM Incubation Tower - Floor 3', 'Pitch Deck Stage, Breakout Discussion Pods, High-Speed WiFi, Coffee Bar', 'images/incubation_hub.jpg')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 3. RESET AUTO_INCREMENT COUNTERS
ALTER TABLE `users` AUTO_INCREMENT = 100;
ALTER TABLE `venues` AUTO_INCREMENT = 100;
ALTER TABLE `events` AUTO_INCREMENT = 100;
ALTER TABLE `registrations` AUTO_INCREMENT = 100;
ALTER TABLE `feedback` AUTO_INCREMENT = 100;
ALTER TABLE `notifications` AUTO_INCREMENT = 100;

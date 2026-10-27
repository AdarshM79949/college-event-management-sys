-- ===================================================================
-- College Event Management System (CampusPulse) - MySQL Schema DDL
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `college_event_db`;
USE `college_event_db`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `college_id` VARCHAR(100),
    `branch` VARCHAR(100),
    `semester` VARCHAR(50),
    `is_verified` BOOLEAN DEFAULT TRUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Venues Table
CREATE TABLE IF NOT EXISTS `venues` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE,
    `type` VARCHAR(100),
    `capacity` INT NOT NULL,
    `location` VARCHAR(255),
    `facilities` TEXT,
    `image_url` VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Events Table
CREATE TABLE IF NOT EXISTS `events` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `tagline` VARCHAR(500),
    `description` TEXT,
    `category` VARCHAR(100) NOT NULL,
    `department` VARCHAR(255),
    `event_date` DATE NOT NULL,
    `event_time` VARCHAR(50) NOT NULL,
    `venue_id` BIGINT,
    `banner_path` VARCHAR(500),
    `registration_deadline` DATE,
    `max_seats` INT NOT NULL DEFAULT 100,
    `registered_count` INT NOT NULL DEFAULT 0,
    `price_type` VARCHAR(50) DEFAULT 'Free',
    `price` DOUBLE DEFAULT 0.0,
    `status` VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    `admin_comment` TEXT,
    `created_by_user_id` BIGINT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_events_venue` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_events_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Registrations Table
CREATE TABLE IF NOT EXISTS `registrations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `event_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `ticket_tier` VARCHAR(100) DEFAULT 'Standard Pass',
    `price_paid` DOUBLE DEFAULT 0.0,
    `status` VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    `qr_token` VARCHAR(255) NOT NULL UNIQUE,
    `checked_in` BOOLEAN DEFAULT FALSE,
    `registered_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `uq_event_student` UNIQUE (`event_id`, `student_id`),
    CONSTRAINT `fk_reg_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reg_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Notifications Table
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `message` TEXT NOT NULL,
    `type` VARCHAR(100),
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Feedback Table
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `event_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `rating` INT NOT NULL CHECK (`rating` >= 1 AND `rating` <= 5),
    `comment` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `uq_feedback_event_student` UNIQUE (`event_id`, `student_id`),
    CONSTRAINT `fk_fb_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_fb_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

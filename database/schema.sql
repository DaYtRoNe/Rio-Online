CREATE DATABASE IF NOT EXISTS `rio`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `rio`;

CREATE TABLE IF NOT EXISTS `day` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_day_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `grade` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `priority` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_grade_name` (`name`),
  UNIQUE KEY `uk_grade_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `subject` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subject_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `grade_has_subject` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `grade_id` INT NOT NULL,
  `day_id` INT NOT NULL,
  `subject_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_grade_day_subject` (`grade_id`, `day_id`, `subject_id`),
  KEY `idx_ghs_day_id` (`day_id`),
  KEY `idx_ghs_subject_id` (`subject_id`),
  CONSTRAINT `fk_ghs_grade` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ghs_day` FOREIGN KEY (`day_id`) REFERENCES `day` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ghs_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `settings` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settings_content` (`content`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `day` (`name`) VALUES
  ('Monday'),
  ('Tuesday'),
  ('Wednesday'),
  ('Thursday'),
  ('Friday'),
  ('Saturday'),
  ('Sunday')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
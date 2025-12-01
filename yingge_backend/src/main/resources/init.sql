-- Schema initialization for yingge_backend
-- Charset: utf8mb4, Engine: InnoDB
create database if not exists yingge_db;
use yingge_db;
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'user id',
    `user_account` VARCHAR(255) NOT NULL COMMENT 'login account',
    `user_password` VARCHAR(255) NOT NULL COMMENT 'hashed password',
    `user_name` VARCHAR(255) DEFAULT NULL COMMENT 'display name',
    `user_avatar` VARCHAR(500) DEFAULT NULL COMMENT 'avatar url',
    `user_profile` TEXT DEFAULT NULL COMMENT 'profile text',
    `user_role` VARCHAR(50) DEFAULT 'user' COMMENT 'role (admin/user)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'created at',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated at',
    `is_delete` TINYINT DEFAULT 0 COMMENT 'soft delete flag (0: active, 1: deleted)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account` (`user_account`),
    KEY `idx_user_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='user table';

CREATE TABLE IF NOT EXISTS `try_on_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'record id',
    `user_id` BIGINT NOT NULL COMMENT 'user id',
    `person_image_url` VARCHAR(500) NOT NULL COMMENT 'original person image url',
    `cloth_image_url` VARCHAR(500) NOT NULL COMMENT 'cloth image url',
    `result_image_url` VARCHAR(500) DEFAULT NULL COMMENT 'try-on result image url',
    `status` VARCHAR(50) DEFAULT 'processing' COMMENT 'processing/done/failed',
    `message` VARCHAR(500) DEFAULT NULL COMMENT 'status or error message',
    `is_delete` TINYINT DEFAULT 0 COMMENT 'soft delete flag',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'created at',
    PRIMARY KEY (`id`),
    KEY `idx_try_on_record_user` (`user_id`),
    KEY `idx_try_on_record_status` (`status`),
    KEY `idx_try_on_record_user_create` (`user_id`, `create_time`),
    CONSTRAINT `fk_try_on_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='try-on record table';

CREATE TABLE IF NOT EXISTS `storage` (
    `storage_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'storage record id',
    `file_name` VARCHAR(255) NOT NULL COMMENT 'original file name',
    `file_path` VARCHAR(500) NOT NULL COMMENT 'stored file url/path',
    `file_type` VARCHAR(50) NOT NULL COMMENT 'person/cloth/result etc.',
    `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'uploaded at',
    PRIMARY KEY (`storage_id`),
    KEY `idx_storage_file_type` (`file_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='image storage table';


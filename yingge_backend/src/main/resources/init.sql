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

CREATE TABLE IF NOT EXISTS `try_on_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'task id',
    `user_id` BIGINT NOT NULL COMMENT 'user id',
    `person_image_url` VARCHAR(500) NOT NULL COMMENT 'person image url',
    `cloth_image_url` VARCHAR(500) NOT NULL COMMENT 'cloth image url',
    `result_image_url` VARCHAR(500) DEFAULT NULL COMMENT 'result image url',
    `prompt` VARCHAR(500) DEFAULT NULL COMMENT 'custom prompt',
    `status` VARCHAR(50) DEFAULT 'pending' COMMENT 'pending/processing/done/failed',
    `error_msg` VARCHAR(500) DEFAULT NULL COMMENT 'error message',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'created at',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated at',
    PRIMARY KEY (`id`),
    KEY `idx_task_user` (`user_id`),
    KEY `idx_task_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='async try-on task table';

CREATE TABLE IF NOT EXISTS `cloth_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'template id',
    `name` VARCHAR(255) NOT NULL COMMENT 'template name',
    `image_url` VARCHAR(500) NOT NULL COMMENT 'cloth image url',
    `mask_url` VARCHAR(500) DEFAULT NULL COMMENT 'optional cutout mask url',
    `category` VARCHAR(100) DEFAULT NULL COMMENT 'category such as wuxia/wedding/modern',
    `style_tags` JSON DEFAULT NULL COMMENT 'style tags as json array text',
    `color` VARCHAR(50) DEFAULT NULL COMMENT 'main color',
    `fit` VARCHAR(50) DEFAULT NULL COMMENT 'fit type',
    `prompt` TEXT DEFAULT NULL COMMENT 'default prompt',
    `negative_prompt` TEXT DEFAULT NULL COMMENT 'default negative prompt',
    `strength` DECIMAL(4,2) DEFAULT NULL COMMENT 'img2img strength',
    `aspect_ratio` VARCHAR(20) DEFAULT NULL COMMENT 'e.g. 3:4',
    `status` TINYINT DEFAULT 1 COMMENT '0=off 1=on 2=gray',
    `sort` INT DEFAULT 0 COMMENT 'manual weight',
    `lang` VARCHAR(16) DEFAULT 'zh-CN' COMMENT 'language code',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'created at',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated at',
    PRIMARY KEY (`id`),
    KEY `idx_template_status` (`status`),
    KEY `idx_template_category` (`category`),
    KEY `idx_template_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='cloth template table';

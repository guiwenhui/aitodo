-- 初始化 AI Todo 数据库脚本
-- 请使用有权限的用户（如 root）执行此脚本

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS aitodo
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE aitodo;

-- 2. 创建用户并授权（如果 root 密码错误，可以创建新用户）
-- 选项 A: 使用 root 用户（确保密码正确）
-- 选项 B: 创建新用户并授权
-- 取消注释下面的行，将 'your_password' 替换为强密码
CREATE USER 'aitodo_user'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON aitodo.* TO 'aitodo_user'@'localhost';
FLUSH PRIVILEGES;

-- 3. 创建表结构（与 schema.sql 相同，但添加 IF NOT EXISTS）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    deadline TIMESTAMP NULL,
    status INT DEFAULT 0 COMMENT '0: pending, 1: completed, 2: delayed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS task_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    delay_time INT COMMENT 'delay time in minutes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- 创建索引
-- DROP INDEX IF EXISTS idx_tasks_user_id ON tasks;
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
-- DROP INDEX IF EXISTS idx_tasks_status ON tasks;
CREATE INDEX idx_tasks_status ON tasks(status);
-- DROP INDEX IF EXISTS idx_task_logs_task_id ON task_logs;
CREATE INDEX idx_task_logs_task_id ON task_logs(task_id);


-- 创建游戏化相关表
CREATE TABLE IF NOT EXISTS user_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_tasks_completed INT DEFAULT 0,
    total_tasks_delayed INT DEFAULT 0,
    total_points INT DEFAULT 0,
    current_level INT DEFAULT 1,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points_change INT NOT NULL,
    reason VARCHAR(200),
    task_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(20),
    points_reward INT DEFAULT 0,
    condition_type VARCHAR(50),
    condition_value INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE
);

-- 4. 插入测试数据（可选）
INSERT IGNORE INTO users (id, username, password) VALUES
(1, 'testuser', 'testpass'),
(2, 'demo', 'demo123');

INSERT IGNORE INTO tasks (id, user_id, title, deadline, status) VALUES
(1, 1, '完成Spring Boot项目', NOW() + INTERVAL 7 DAY, 0),
(2, 1, '学习MyBatis', NOW() + INTERVAL 3 DAY, 1),
(3, 2, '编写文档', NOW() + INTERVAL 1 DAY, 0);

INSERT IGNORE INTO task_logs (id, task_id, delay_time) VALUES
(1, 3, 120); -- 任务3延迟120分钟

-- 插入游戏化测试数据
-- 初始化用户统计数据
INSERT IGNORE INTO user_stats (user_id, total_tasks_completed, total_tasks_delayed, total_points, current_level) VALUES
(1, 1, 0, 50, 1), -- testuser 已完成1个任务
(2, 0, 0, 0, 1);  -- demo 用户

-- 插入成就定义
INSERT IGNORE INTO achievements (id, name, description, icon, points_reward, condition_type, condition_value) VALUES
(1, '新手起步', '完成第一个任务', '🏆', 50, 'total_completed_tasks', 1),
(2, '效率达人', '完成10个任务', '⚡', 200, 'total_completed_tasks', 10),
(3, '拖延克星', '连续3天没有拖延任务', '🦸', 150, 'no_delayed_days', 3),
(4, '积分高手', '获得1000积分', '💰', 300, 'total_points', 1000),
(5, '升级达人', '达到等级5', '⭐', 500, 'level', 5),
(6, '一周之星', '一周内完成7个任务', '🌟', 400, 'weekly_completed_tasks', 7);

-- 为用户分配已解锁的成就（testuser已完成1个任务，解锁新手起步）
INSERT IGNORE INTO user_achievements (user_id, achievement_id) VALUES
(1, 1);

-- 5. 显示创建的表
SHOW TABLES;

-- 6. 显示各表数据量
SELECT 'users' AS table_name, COUNT(*) AS row_count FROM users
UNION ALL
SELECT 'tasks', COUNT(*) FROM tasks
UNION ALL
SELECT 'task_logs', COUNT(*) FROM task_logs
UNION ALL
SELECT 'user_stats', COUNT(*) FROM user_stats
UNION ALL
SELECT 'user_points', COUNT(*) FROM user_points
UNION ALL
SELECT 'achievements', COUNT(*) FROM achievements
UNION ALL
SELECT 'user_achievements', COUNT(*) FROM user_achievements;
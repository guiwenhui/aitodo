-- MySQL Schema for AI Todo + 拖延症治疗器
-- This schema is compatible with MySQL 5.7+ and MySQL 8.0+

-- 1. 创建 users 表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL COMMENT '用户头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 创建 tasks 表
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    deadline TIMESTAMP NULL,
    status INT DEFAULT 0 COMMENT '0: pending, 1: completed, 2: delayed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. 创建 task_logs 表
CREATE TABLE IF NOT EXISTS task_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    delay_time INT COMMENT 'delay time in minutes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- 4. 创建 user_stats 表
CREATE TABLE IF NOT EXISTS user_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_tasks_completed INT DEFAULT 0 COMMENT '总完成任务数',
    total_tasks_delayed INT DEFAULT 0 COMMENT '总拖延任务数',
    total_points INT DEFAULT 0 COMMENT '总积分',
    current_level INT DEFAULT 1 COMMENT '当前等级',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_stats_user_id (user_id)
);

-- 5. 创建 user_points 表
CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points_change INT NOT NULL COMMENT '积分变化值（正数为增加，负数为减少）',
    reason VARCHAR(100) NOT NULL COMMENT '积分变动原因',
    task_id BIGINT COMMENT '关联的任务ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
);

-- 6. 创建 achievements 表
CREATE TABLE IF NOT EXISTS achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '成就名称',
    description VARCHAR(200) COMMENT '成就描述',
    icon VARCHAR(50) COMMENT '成就图标（emoji或图标类名）',
    points_reward INT DEFAULT 0 COMMENT '成就奖励积分',
    condition_type VARCHAR(50) NOT NULL COMMENT '成就条件类型（如：total_completed_tasks, total_points, level）',
    condition_value INT NOT NULL COMMENT '成就条件值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. 创建 user_achievements 表
CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_achievement (user_id, achievement_id)
);

-- 8. 创建索引
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_task_logs_task_id ON task_logs(task_id);
CREATE INDEX idx_user_stats_user_id ON user_stats(user_id);
CREATE INDEX idx_user_points_user_id ON user_points(user_id);
CREATE INDEX idx_user_points_created_at ON user_points(created_at);
CREATE INDEX idx_user_achievements_user_id ON user_achievements(user_id);

-- 9. 插入测试数据 (MySQL兼容语法)
INSERT INTO users (id, username, password, created_at) VALUES
(1, 'testuser', 'testpass', CURRENT_TIMESTAMP),
(2, 'demo', 'demo123', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username=VALUES(username);

INSERT INTO tasks (id, user_id, title, deadline, status, created_at) VALUES
(1, 1, '完成Spring Boot项目', DATE_ADD(NOW(), INTERVAL 7 DAY), 0, CURRENT_TIMESTAMP),
(2, 1, '学习MyBatis', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, CURRENT_TIMESTAMP),
(3, 2, '编写文档', DATE_ADD(NOW(), INTERVAL 1 DAY), 0, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO task_logs (id, task_id, delay_time, created_at) VALUES
(1, 3, 120, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE delay_time=VALUES(delay_time);

-- 初始化用户统计数据
INSERT INTO user_stats (user_id, total_tasks_completed, total_tasks_delayed, total_points, current_level) VALUES
(1, 1, 0, 50, 1),
(2, 0, 0, 0, 1)
ON DUPLICATE KEY UPDATE total_tasks_completed=VALUES(total_tasks_completed);

-- 插入成就定义
INSERT INTO achievements (id, name, description, icon, points_reward, condition_type, condition_value) VALUES
(1, '新手起步', '完成第一个任务', '🏆', 50, 'total_completed_tasks', 1),
(2, '效率达人', '完成10个任务', '⚡', 200, 'total_completed_tasks', 10),
(3, '拖延克星', '连续3天没有拖延任务', '🦸', 150, 'no_delayed_days', 3),
(4, '积分高手', '获得1000积分', '💰', 300, 'total_points', 1000),
(5, '升级达人', '达到等级5', '⭐', 500, 'level', 5),
(6, '一周之星', '一周内完成7个任务', '🌟', 400, 'weekly_completed_tasks', 7)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 为用户分配已解锁的成就（testuser已完成1个任务，解锁新手起步）
INSERT INTO user_achievements (user_id, achievement_id) VALUES
(1, 1)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);
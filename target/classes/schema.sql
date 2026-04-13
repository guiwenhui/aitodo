-- Database: aitodo
-- Create tables for AI Todo + 拖延症治疗器

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
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

-- Indexes for better performance
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_task_logs_task_id ON task_logs(task_id);

-- 游戏化功能表（可视化和积分系统）
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

CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_achievement (user_id, achievement_id)
);

-- 游戏化功能索引
CREATE INDEX idx_user_stats_user_id ON user_stats(user_id);
CREATE INDEX idx_user_points_user_id ON user_points(user_id);
CREATE INDEX idx_user_points_created_at ON user_points(created_at);
CREATE INDEX idx_user_achievements_user_id ON user_achievements(user_id);

-- 插入测试数据 (H2 compatible)
MERGE INTO users KEY (id) VALUES (1, 'testuser', 'testpass', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (2, 'demo', 'demo123', CURRENT_TIMESTAMP);

MERGE INTO tasks KEY (id) VALUES (1, 1, '完成Spring Boot项目', DATEADD('DAY', 7, NOW()), 0, CURRENT_TIMESTAMP);
MERGE INTO tasks KEY (id) VALUES (2, 1, '学习MyBatis', DATEADD('DAY', 3, NOW()), 1, CURRENT_TIMESTAMP);
MERGE INTO tasks KEY (id) VALUES (3, 2, '编写文档', DATEADD('DAY', 1, NOW()), 0, CURRENT_TIMESTAMP);

MERGE INTO task_logs KEY (id) VALUES (1, 3, 120, CURRENT_TIMESTAMP);
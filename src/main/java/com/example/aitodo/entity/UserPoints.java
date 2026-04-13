package com.example.aitodo.entity;

import java.time.LocalDateTime;

/**
 * 用户积分记录实体类
 * 记录每次积分变动的原因和数值
 */
public class UserPoints {
    private Long id;
    private Long userId;
    private Integer pointsChange;     // 积分变化值（正数为增加，负数为减少）
    private String reason;            // 积分变动原因
    private Long taskId;              // 关联的任务ID（可选）
    private LocalDateTime createdAt;

    public UserPoints() {
    }

    public UserPoints(Long id, Long userId, Integer pointsChange,
                     String reason, Long taskId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.pointsChange = pointsChange;
        this.reason = reason;
        this.taskId = taskId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPointsChange() {
        return pointsChange;
    }

    public void setPointsChange(Integer pointsChange) {
        this.pointsChange = pointsChange;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 工厂方法：创建完成任务积分记录
    public static UserPoints createTaskCompletedRecord(Long userId, Long taskId, int points) {
        UserPoints record = new UserPoints();
        record.setUserId(userId);
        record.setTaskId(taskId);
        record.setPointsChange(points);
        record.setReason("完成任务奖励");
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    // 工厂方法：创建拖延任务积分记录
    public static UserPoints createTaskDelayedRecord(Long userId, Long taskId, int points) {
        UserPoints record = new UserPoints();
        record.setUserId(userId);
        record.setTaskId(taskId);
        record.setPointsChange(-points); // 负值
        record.setReason("任务拖延扣除");
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    // 工厂方法：创建成就解锁积分记录
    public static UserPoints createAchievementUnlockedRecord(Long userId, String achievementName, int points) {
        UserPoints record = new UserPoints();
        record.setUserId(userId);
        record.setPointsChange(points);
        record.setReason("成就解锁: " + achievementName);
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    @Override
    public String toString() {
        return "UserPoints{" +
                "id=" + id +
                ", userId=" + userId +
                ", pointsChange=" + pointsChange +
                ", reason='" + reason + '\'' +
                ", taskId=" + taskId +
                ", createdAt=" + createdAt +
                '}';
    }
}
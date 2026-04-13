package com.example.aitodo.entity;

import java.time.LocalDateTime;

/**
 * 用户统计数据实体类
 * 存储用户的游戏化统计数据：完成任务数、拖延次数、积分、等级等
 */
public class UserStats {
    private Long id;
    private Long userId;
    private Integer totalTasksCompleted = 0;     // 总完成任务数
    private Integer totalTasksDelayed = 0;       // 总拖延任务数
    private Integer totalPoints = 0;             // 总积分
    private Integer currentLevel = 1;            // 当前等级
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;

    public UserStats() {
    }

    public UserStats(Long id, Long userId, Integer totalTasksCompleted,
                    Integer totalTasksDelayed, Integer totalPoints,
                    Integer currentLevel, LocalDateTime lastUpdated,
                    LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalTasksCompleted = totalTasksCompleted;
        this.totalTasksDelayed = totalTasksDelayed;
        this.totalPoints = totalPoints;
        this.currentLevel = currentLevel;
        this.lastUpdated = lastUpdated;
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

    public Integer getTotalTasksCompleted() {
        return totalTasksCompleted;
    }

    public void setTotalTasksCompleted(Integer totalTasksCompleted) {
        this.totalTasksCompleted = totalTasksCompleted;
    }

    public Integer getTotalTasksDelayed() {
        return totalTasksDelayed;
    }

    public void setTotalTasksDelayed(Integer totalTasksDelayed) {
        this.totalTasksDelayed = totalTasksDelayed;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 工具方法：增加完成任务数
    public void incrementTasksCompleted(int count) {
        this.totalTasksCompleted += count;
        this.lastUpdated = LocalDateTime.now();
    }

    // 工具方法：增加拖延任务数
    public void incrementTasksDelayed(int count) {
        this.totalTasksDelayed += count;
        this.lastUpdated = LocalDateTime.now();
    }

    // 工具方法：增加积分
    public void addPoints(int points) {
        this.totalPoints += points;
        this.lastUpdated = LocalDateTime.now();
    }

    // 工具方法：计算等级（根据积分）
    public void calculateLevel() {
        // 简单的等级计算：每100积分升1级，从1级开始
        this.currentLevel = Math.max(1, this.totalPoints / 100 + 1);
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalTasksCompleted=" + totalTasksCompleted +
                ", totalTasksDelayed=" + totalTasksDelayed +
                ", totalPoints=" + totalPoints +
                ", currentLevel=" + currentLevel +
                ", lastUpdated=" + lastUpdated +
                ", createdAt=" + createdAt +
                '}';
    }
}
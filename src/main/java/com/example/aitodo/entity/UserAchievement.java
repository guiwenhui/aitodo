package com.example.aitodo.entity;

import java.time.LocalDateTime;

/**
 * 用户成就关联实体类
 * 记录用户已解锁的成就
 */
public class UserAchievement {
    private Long id;
    private Long userId;
    private Long achievementId;
    private LocalDateTime unlockedAt;

    // 关联的成就对象（用于查询时关联）
    private Achievement achievement;

    public UserAchievement() {
    }

    public UserAchievement(Long id, Long userId, Long achievementId,
                          LocalDateTime unlockedAt) {
        this.id = id;
        this.userId = userId;
        this.achievementId = achievementId;
        this.unlockedAt = unlockedAt;
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

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    // 工厂方法：创建用户成就记录
    public static UserAchievement create(Long userId, Long achievementId) {
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUserId(userId);
        userAchievement.setAchievementId(achievementId);
        userAchievement.setUnlockedAt(LocalDateTime.now());
        return userAchievement;
    }

    @Override
    public String toString() {
        return "UserAchievement{" +
                "id=" + id +
                ", userId=" + userId +
                ", achievementId=" + achievementId +
                ", unlockedAt=" + unlockedAt +
                ", achievement=" + (achievement != null ? achievement.getName() : "null") +
                '}';
    }
}
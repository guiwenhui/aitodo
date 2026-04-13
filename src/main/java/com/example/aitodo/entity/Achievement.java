package com.example.aitodo.entity;

import java.time.LocalDateTime;

/**
 * 成就定义实体类
 * 定义可解锁的成就及其条件
 */
public class Achievement {
    private Long id;
    private String name;                // 成就名称
    private String description;         // 成就描述
    private String icon;                // 成就图标（emoji或图标类名）
    private Integer pointsReward;       // 成就奖励积分
    private String conditionType;       // 成就条件类型
    private Integer conditionValue;     // 成就条件值
    private LocalDateTime createdAt;

    public Achievement() {
    }

    public Achievement(Long id, String name, String description,
                      String icon, Integer pointsReward,
                      String conditionType, Integer conditionValue,
                      LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.pointsReward = pointsReward;
        this.conditionType = conditionType;
        this.conditionValue = conditionValue;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getPointsReward() {
        return pointsReward;
    }

    public void setPointsReward(Integer pointsReward) {
        this.pointsReward = pointsReward;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public Integer getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(Integer conditionValue) {
        this.conditionValue = conditionValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 检查成就条件是否满足
    public boolean isConditionMet(int currentValue) {
        return currentValue >= conditionValue;
    }

    // 获取条件类型描述
    public String getConditionTypeDescription() {
        switch (conditionType) {
            case "total_completed_tasks":
                return "累计完成任务数";
            case "total_points":
                return "累计积分";
            case "level":
                return "用户等级";
            case "no_delayed_days":
                return "连续无拖延天数";
            case "weekly_completed_tasks":
                return "周完成任务数";
            default:
                return conditionType;
        }
    }

    // 获取条件进度文本
    public String getConditionProgressText(int currentValue) {
        String desc = getConditionTypeDescription();
        int target = conditionValue;
        int progress = Math.min(currentValue, target);
        return String.format("%s: %d/%d", desc, progress, target);
    }

    // 获取进度百分比
    public int getProgressPercentage(int currentValue) {
        if (conditionValue <= 0) return 100;
        int progress = Math.min(currentValue, conditionValue);
        return (int) ((progress * 100.0) / conditionValue);
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", pointsReward=" + pointsReward +
                ", conditionType='" + conditionType + '\'' +
                ", conditionValue=" + conditionValue +
                ", createdAt=" + createdAt +
                '}';
    }
}
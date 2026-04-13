package com.example.aitodo.dto;

/**
 * 可视化统计数据传输对象
 */
public class VisualizationStatsDTO {
    private Integer completedTasks;  // 完成任务数
    private Integer delayedTasks;    // 拖延任务数
    private Integer points;          // 积分
    private Integer level;           // 等级

    public VisualizationStatsDTO() {
    }

    public VisualizationStatsDTO(Integer completedTasks, Integer delayedTasks, Integer points, Integer level) {
        this.completedTasks = completedTasks;
        this.delayedTasks = delayedTasks;
        this.points = points;
        this.level = level;
    }

    // Getters and Setters
    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Integer getDelayedTasks() {
        return delayedTasks;
    }

    public void setDelayedTasks(Integer delayedTasks) {
        this.delayedTasks = delayedTasks;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "VisualizationStatsDTO{" +
                "completedTasks=" + completedTasks +
                ", delayedTasks=" + delayedTasks +
                ", points=" + points +
                ", level=" + level +
                '}';
    }
}
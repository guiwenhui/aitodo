package com.example.aitodo.entity;

import java.time.LocalDateTime;

public class TaskLog {
    private Long id;
    private Long taskId;
    private Integer delayTime; // delay time in minutes
    private LocalDateTime createdAt;

    public TaskLog() {
    }

    public TaskLog(Long id, Long taskId, Integer delayTime) {
        this.id = id;
        this.taskId = taskId;
        this.delayTime = delayTime;
    }

    public TaskLog(Long id, Long taskId, Integer delayTime, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.delayTime = delayTime;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
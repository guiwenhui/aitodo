package com.example.aitodo.service.impl;

import com.example.aitodo.entity.Task;
import com.example.aitodo.entity.TaskLog;
import com.example.aitodo.mapper.TaskLogMapper;
import com.example.aitodo.mapper.TaskMapper;
import com.example.aitodo.service.TaskLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskLogServiceImpl implements TaskLogService {

    private final TaskLogMapper taskLogMapper;
    private final TaskMapper taskMapper;

    public TaskLogServiceImpl(TaskLogMapper taskLogMapper, TaskMapper taskMapper) {
        this.taskLogMapper = taskLogMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskLog createTaskLog(TaskLog taskLog) {
        taskLogMapper.insert(taskLog);
        return taskLog;
    }

    @Override
    public TaskLog getTaskLogById(Long id) {
        return taskLogMapper.selectById(id);
    }

    @Override
    public List<TaskLog> getAllTaskLogs() {
        return taskLogMapper.selectAll();
    }

    @Override
    public List<TaskLog> getTaskLogsByTaskId(Long taskId) {
        return taskLogMapper.selectByTaskId(taskId);
    }

    @Override
    public boolean deleteTaskLog(Long id) {
        TaskLog log = taskLogMapper.selectById(id);
        if (log == null) {
            return false;
        }
        taskLogMapper.deleteById(id);
        return true;
    }

    @Override
    public int getTotalDelayTime(Long taskId) {
        List<TaskLog> logs = taskLogMapper.selectByTaskId(taskId);
        return logs.stream()
                .mapToInt(TaskLog::getDelayTime)
                .sum();
    }

    @Override
    public double getAverageDelayTime(Long userId) {
        // Get all tasks for the user
        List<Task> tasks = taskMapper.selectByUserId(userId);
        if (tasks.isEmpty()) {
            return 0.0;
        }

        // Calculate total delay time across all tasks
        int totalDelay = 0;
        int taskCount = 0;

        for (Task task : tasks) {
            List<TaskLog> logs = taskLogMapper.selectByTaskId(task.getId());
            if (!logs.isEmpty()) {
                int taskDelay = logs.stream().mapToInt(TaskLog::getDelayTime).sum();
                totalDelay += taskDelay;
                taskCount++;
            }
        }

        if (taskCount == 0) {
            return 0.0;
        }

        return (double) totalDelay / taskCount;
    }
}
package com.example.aitodo.service;

import com.example.aitodo.entity.TaskLog;
import java.util.List;

public interface TaskLogService {

    /**
     * Create task log
     * @param taskLog task log object
     * @return created task log with id
     */
    TaskLog createTaskLog(TaskLog taskLog);

    /**
     * Get task log by id
     * @param id task log id
     * @return task log object
     */
    TaskLog getTaskLogById(Long id);

    /**
     * Get all task logs
     * @return list of all task logs
     */
    List<TaskLog> getAllTaskLogs();

    /**
     * Get task logs by task id
     * @param taskId task id
     * @return list of task logs for the task
     */
    List<TaskLog> getTaskLogsByTaskId(Long taskId);

    /**
     * Delete task log by id
     * @param id task log id
     * @return true if deleted successfully
     */
    boolean deleteTaskLog(Long id);

    /**
     * Get total delay time for a task
     * @param taskId task id
     * @return total delay time in minutes
     */
    int getTotalDelayTime(Long taskId);

    /**
     * Get average delay time for a user's tasks
     * @param userId user id
     * @return average delay time in minutes
     */
    double getAverageDelayTime(Long userId);
}
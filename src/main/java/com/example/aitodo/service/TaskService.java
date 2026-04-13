package com.example.aitodo.service;

import com.example.aitodo.entity.Task;
import java.util.List;

public interface TaskService {

    /**
     * Create a new task
     * @param task task object
     * @return created task with id
     */
    Task createTask(Task task);

    /**
     * Update task
     * @param task task object
     * @return updated task
     */
    Task updateTask(Task task);

    /**
     * Delete task by id
     * @param id task id
     * @return true if deleted successfully
     */
    boolean deleteTask(Long id);

    /**
     * Get task by id
     * @param id task id
     * @return task object
     */
    Task getTaskById(Long id);

    /**
     * Get all tasks
     * @return list of all tasks
     */
    List<Task> getAllTasks();

    /**
     * Get tasks by user id
     * @param userId user id
     * @return list of tasks for the user
     */
    List<Task> getTasksByUserId(Long userId);

    /**
     * Get tasks by status
     * @param status task status (0: pending, 1: completed, 2: delayed)
     * @return list of tasks with specified status
     */
    List<Task> getTasksByStatus(Integer status);

    /**
     * Mark task as completed
     * @param id task id
     * @return updated task
     */
    Task markTaskCompleted(Long id);

    /**
     * Mark task as delayed
     * @param id task id
     * @return updated task
     */
    Task markTaskDelayed(Long id);

    /**
     * Get overdue tasks (deadline passed and status is pending)
     * @return list of overdue tasks
     */
    List<Task> getOverdueTasks();

    /**
     * Get task completion rate for a user
     * @param userId user id
     * @return completion rate (0.0 to 1.0)
     */
    double getCompletionRate(Long userId);

    /**
     * Generate AI warning/reminder text for a task
     * @param task task object
     * @return AI-generated warning text with different styles:
     *   - Delayed tasks: humorous/sarcastic
     *   - Almost due tasks: warning
     *   - Completed tasks: normal prompt
     */
    String aiWarning(Task task);
}
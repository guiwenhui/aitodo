package com.example.aitodo.service.impl;

import com.example.aitodo.entity.Task;
import com.example.aitodo.entity.TaskLog;
import com.example.aitodo.mapper.TaskLogMapper;
import com.example.aitodo.mapper.TaskMapper;
import com.example.aitodo.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskLogMapper taskLogMapper;
    private final com.example.aitodo.mapper.UserStatsMapper userStatsMapper;

    public TaskServiceImpl(TaskMapper taskMapper, TaskLogMapper taskLogMapper, com.example.aitodo.mapper.UserStatsMapper userStatsMapper) {
        this.taskMapper = taskMapper;
        this.taskLogMapper = taskLogMapper;
        this.userStatsMapper = userStatsMapper;
    }

    @Override
    public Task createTask(Task task) {
        // Set created time if not provided
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        // Default status is pending (0)
        if (task.getStatus() == null) {
            task.setStatus(0);
        }
        // User ID must be provided
        if (task.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        taskMapper.insert(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        Task existing = taskMapper.selectById(task.getId());
        if (existing == null) {
            throw new RuntimeException("Task not found with id: " + task.getId());
        }

        taskMapper.update(task);
        return task;
    }

    @Override
    public boolean deleteTask(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            return false;
        }
        taskMapper.deleteById(id);
        return true;
    }

    @Override
    public Task getTaskById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskMapper.selectAll();
    }

    @Override
    public List<Task> getTasksByUserId(Long userId) {
        return taskMapper.selectByUserId(userId);
    }

    @Override
    public List<Task> getTasksByStatus(Integer status) {
        return taskMapper.selectByStatus(status);
    }

    @Override
    public Task markTaskCompleted(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + id);
        }

        // Avoid duplicate point assignments if already completed
        if (task.getStatus() != null && task.getStatus() == 1) {
            return task;
        }

        task.setStatus(1); // completed
        taskMapper.update(task);

        // Update UserStats
        if (task.getUserId() != null) {
            // Check if user has stats initialized
            com.example.aitodo.entity.UserStats stats = userStatsMapper.selectByUserId(task.getUserId());
            if (stats == null) {
                userStatsMapper.initUserStats(task.getUserId());
                stats = userStatsMapper.selectByUserId(task.getUserId());
            }

            // Increment completed tasks
            userStatsMapper.incrementTasksCompleted(task.getUserId(), 1);

            // Calculate points: base 10
            int pointsEarned = 10;
            // Bonus points if finished before deadline
            if (task.getDeadline() != null && LocalDateTime.now().isBefore(task.getDeadline())) {
                pointsEarned += 5;
            }

            userStatsMapper.addPoints(task.getUserId(), pointsEarned);

            // Leveling logic (1 level per 50 points after update)
            int currentPoints = stats.getTotalPoints() + pointsEarned;
            int newLevel = (currentPoints / 50) + 1;
            if (newLevel > stats.getCurrentLevel()) {
                userStatsMapper.updateLevel(task.getUserId(), newLevel);
            }
        }

        return task;
    }

    @Override
    public Task markTaskDelayed(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + id);
        }

        task.setStatus(2); // delayed
        taskMapper.update(task);

        // Record delay log (optional)
        TaskLog log = new TaskLog();
        log.setTaskId(id);
        // Calculate delay time in minutes (simplified)
        if (task.getDeadline() != null) {
            long delayMinutes = java.time.Duration.between(task.getDeadline(), LocalDateTime.now()).toMinutes();
            if (delayMinutes > 0) {
                log.setDelayTime((int) delayMinutes);
            }
        }
        taskLogMapper.insert(log);

        return task;
    }

    @Override
    public List<Task> getOverdueTasks() {
        return taskMapper.selectOverdueTasks();
    }

    @Override
    public double getCompletionRate(Long userId) {
        List<Task> allTasks = taskMapper.selectByUserId(userId);
        if (allTasks.isEmpty()) {
            return 0.0;
        }

        long completedCount = allTasks.stream()
                .filter(task -> task.getStatus() == 1) // completed
                .count();

        return (double) completedCount / allTasks.size();
    }

    @Override
    public String aiWarning(Task task) {
        if (task == null) {
            return "⚠️ 任务不存在，请检查任务ID";
        }

        Integer status = task.getStatus();
        LocalDateTime deadline = task.getDeadline();
        String title = task.getTitle();

        // 任务已完成
        if (status != null && status == 1) {
            return String.format("✅ 恭喜！任务「%s」已完成。继续保持高效工作！", title);
        }

        // 任务已标记为延迟
        if (status != null && status == 2) {
            return generateDelayedWarning(title, deadline);
        }

        // 任务待处理，检查截止时间
        if (deadline == null) {
            return String.format("📝 任务「%s」没有设置截止时间。建议设置截止时间以提高效率！", title);
        }

        LocalDateTime now = LocalDateTime.now();
        long hoursUntilDeadline = java.time.Duration.between(now, deadline).toHours();

        // 已过期
        if (hoursUntilDeadline < 0) {
            long overdueHours = -hoursUntilDeadline;
            return generateOverdueWarning(title, overdueHours);
        }

        // 快到期（24小时内）
        if (hoursUntilDeadline <= 24) {
            return generateUrgentWarning(title, hoursUntilDeadline);
        }

        // 正常待处理任务
        long daysUntilDeadline = hoursUntilDeadline / 24;
        return generateNormalReminder(title, daysUntilDeadline);
    }

    /**
     * 生成延迟任务的幽默/毒舌提醒
     */
    private String generateDelayedWarning(String title, LocalDateTime deadline) {
        String[] sarcasticMessages = {
            "🐌 任务「%s」已经延迟了，还在等什么？等它自己完成吗？",
            "⏰ 嘿，拖延大师！任务「%s」的截止时间早就过了，是不是在创造新的拖延记录？",
            "📅 任务「%s」已经加入「拖延俱乐部」VIP会员，续费方式是立即开始工作！",
            "😅 我猜任务「%s」还在「等我准备好」的状态？醒醒，时间不会等你的！",
            "🤔 任务「%s」：状态「延迟中」→ 解决方案「现在开始」"
        };

        String message = sarcasticMessages[(int) (System.currentTimeMillis() % sarcasticMessages.length)];
        if (deadline != null) {
            long overdueHours = java.time.Duration.between(deadline, LocalDateTime.now()).toHours();
            if (overdueHours > 0) {
                return String.format(message + "（已延迟 %d 小时）", title, overdueHours);
            }
        }
        return String.format(message, title);
    }

    /**
     * 生成过期任务的警告
     */
    private String generateOverdueWarning(String title, long overdueHours) {
        if (overdueHours < 24) {
            return String.format("🚨 紧急！任务「%s」已过期 %d 小时，现在补救还来得及！", title, overdueHours);
        } else {
            long overdueDays = overdueHours / 24;
            return String.format("🔥 严重警告！任务「%s」已过期 %d 天，拖延成本正在指数增长！", title, overdueDays);
        }
    }

    /**
     * 生成紧急提醒（24小时内到期）
     */
    private String generateUrgentWarning(String title, long hoursLeft) {
        if (hoursLeft <= 1) {
            return String.format("⏱️ 最后冲刺！任务「%s」只剩不到1小时，加油！", title);
        } else if (hoursLeft <= 3) {
            return String.format("⚠️ 高度紧急！任务「%s」只剩 %d 小时，立即行动！", title, hoursLeft);
        } else if (hoursLeft <= 6) {
            return String.format("❗ 紧急提醒！任务「%s」还剩 %d 小时，别等到最后一刻！", title, hoursLeft);
        } else {
            return String.format("📢 注意！任务「%s」今天内到期（剩余 %d 小时），建议现在处理。", title, hoursLeft);
        }
    }

    /**
     * 生成正常任务的提醒
     */
    private String generateNormalReminder(String title, long daysLeft) {
        if (daysLeft == 0) {
            return String.format("👀 任务「%s」今天到期，建议优先处理。", title);
        } else if (daysLeft == 1) {
            return String.format("📅 任务「%s」明天到期，提前规划避免匆忙。", title);
        } else if (daysLeft <= 3) {
            return String.format("🗓️ 任务「%s」还剩 %d 天，建议尽早开始。", title, daysLeft);
        } else if (daysLeft <= 7) {
            return String.format("📋 任务「%s」还有 %d 天时间，合理安排进度。", title, daysLeft);
        } else {
            return String.format("📝 任务「%s」距离截止还有 %d 天，保持良好节奏。", title, daysLeft);
        }
    }
}
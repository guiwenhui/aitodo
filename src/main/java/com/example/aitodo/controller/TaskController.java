package com.example.aitodo.controller;

import com.example.aitodo.entity.Task;
import com.example.aitodo.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 检查用户是否已登录
     */
    private Long getCurrentUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    /**
     * 检查是否有权限访问任务（任务属于当前用户）
     */
    private boolean hasPermission(Task task, Long currentUserId) {
        return task != null && task.getUserId() != null && task.getUserId().equals(currentUserId);
    }

    /**
     * 检查是否有权限访问任务（通过ID）
     */
    private boolean hasPermission(Long taskId, Long currentUserId) {
        Task task = taskService.getTaskById(taskId);
        return hasPermission(task, currentUserId);
    }


    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            // 确保任务属于当前用户
            task.setUserId(currentUserId);
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            if (!hasPermission(id, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权访问此任务"));
            }

            task.setId(id); // Ensure ID matches path variable
            // 确保任务属于当前用户
            task.setUserId(currentUserId);
            Task updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        if (!hasPermission(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权访问此任务"));
        }

        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            return ResponseEntity.ok(createSuccessResponse("Task deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Task not found"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Task not found"));
        }

        if (!hasPermission(task, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权访问此任务"));
        }

        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        List<Task> tasks = taskService.getTasksByUserId(currentUserId);
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable Integer status, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        List<Task> allUserTasks = taskService.getTasksByUserId(currentUserId);
        List<Task> filteredTasks = allUserTasks.stream()
                .filter(task -> status.equals(task.getStatus()))
                .toList();
        return ResponseEntity.ok(filteredTasks);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> markTaskCompleted(@PathVariable Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            if (!hasPermission(id, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权访问此任务"));
            }

            Task task = taskService.markTaskCompleted(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/delay")
    public ResponseEntity<?> markTaskDelayed(@PathVariable Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            if (!hasPermission(id, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权访问此任务"));
            }

            Task task = taskService.markTaskDelayed(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueTasks(HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        List<Task> allUserTasks = taskService.getTasksByUserId(currentUserId);
        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueTasks = allUserTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .toList();
        return ResponseEntity.ok(overdueTasks);
    }



    @PostMapping("/add")
    public ResponseEntity<?> addTask(@RequestBody Task task, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            // 确保任务属于当前用户
            task.setUserId(currentUserId);
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/status")
    public ResponseEntity<?> statusTasks(@RequestParam Integer status, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            List<Task> allUserTasks = taskService.getTasksByUserId(currentUserId);
            List<Task> filteredTasks = allUserTasks.stream()
                    .filter(task -> status.equals(task.getStatus()))
                    .toList();
            return ResponseEntity.ok(filteredTasks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/finish")
    public ResponseEntity<?> finishTask(@RequestParam Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            if (!hasPermission(id, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权访问此任务"));
            }

            Task task = taskService.markTaskCompleted(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/ai-warning")
    public ResponseEntity<?> getAiWarning(@PathVariable Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            Task task = taskService.getTaskById(id);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Task not found with id: " + id));
            }

            if (!hasPermission(task, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权访问此任务"));
            }

            String warningText = taskService.aiWarning(task);
            // 检查AI警告文本是否为空
            if (warningText == null || warningText.trim().isEmpty()) {
                warningText = "⚠️ 未能生成AI警告，请稍后重试";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", id);
            response.put("title", task.getTitle());
            response.put("status", task.getStatus());
            response.put("deadline", task.getDeadline());
            response.put("aiWarning", warningText);
            response.put("warningStyle", getWarningStyle(task));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 根据任务状态和截止时间确定警告风格
     */
    private String getWarningStyle(Task task) {
        Integer status = task.getStatus();
        LocalDateTime deadline = task.getDeadline();

        if (status == null) {
            return "unknown";
        }

        switch (status) {
            case 1: // completed
                return "completed";
            case 2: // delayed
                return "delayed_humorous";
            default: // pending
                if (deadline == null) {
                    return "pending_no_deadline";
                }

                LocalDateTime now = LocalDateTime.now();
                long hoursUntilDeadline = java.time.Duration.between(now, deadline).toHours();

                if (hoursUntilDeadline < 0) {
                    return "overdue";
                } else if (hoursUntilDeadline <= 24) {
                    return "urgent_warning";
                } else {
                    return "normal_reminder";
                }
        }
    }


    @GetMapping("/my-ai-warnings")
    public ResponseEntity<?> getMyAiWarnings(HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            List<Task> tasks = taskService.getTasksByUserId(currentUserId);
            Map<String, Object> response = buildAiWarningsResponse(currentUserId, tasks);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-completion-rate")
    public ResponseEntity<?> getMyCompletionRate(HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            double rate = taskService.getCompletionRate(currentUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", currentUserId);
            response.put("completionRate", rate);
            response.put("percentage", String.format("%.2f%%", rate * 100));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-stats")
    public ResponseEntity<?> getMyTaskStats(HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            List<Task> allTasks = taskService.getTasksByUserId(currentUserId);
            long totalTasks = allTasks.size();
            long pendingTasks = allTasks.stream().filter(t -> t.getStatus() == 0).count();
            long completedTasks = allTasks.stream().filter(t -> t.getStatus() == 1).count();
            long delayedTasks = allTasks.stream().filter(t -> t.getStatus() == 2).count();
            double completionRate = taskService.getCompletionRate(currentUserId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", currentUserId);
            stats.put("totalTasks", totalTasks);
            stats.put("pendingTasks", pendingTasks);
            stats.put("completedTasks", completedTasks);
            stats.put("delayedTasks", delayedTasks);
            stats.put("completionRate", completionRate);
            stats.put("completionPercentage", String.format("%.2f%%", completionRate * 100));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 生成用户任务总体摘要
     */
    private String generateSummary(int urgentCount, int delayedCount, int completedCount, int totalTasks) {
        if (totalTasks == 0) {
            return "📭 你还没有创建任何任务，开始制定计划吧！";
        }

        if (urgentCount > 0) {
            return String.format("🚨 注意！你有 %d 个紧急任务需要立即处理！", urgentCount);
        } else if (delayedCount > 0) {
            return String.format("⏰ 你有 %d 个任务已延迟，建议尽快处理。", delayedCount);
        } else if (completedCount == totalTasks) {
            return "🎉 太棒了！所有任务都已完成，继续保持！";
        } else {
            int pendingCount = totalTasks - completedCount;
            return String.format("📊 进度良好！%d 个任务待完成，%d 个已完成。", pendingCount, completedCount);
        }
    }

    /**
     * 构建用户AI警告响应
     */
    private Map<String, Object> buildAiWarningsResponse(Long userId, List<Task> tasks) {
        if (tasks.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("message", "用户没有任务");
            response.put("aiWarnings", new ArrayList<>());
            return response;
        }

        List<Map<String, Object>> warnings = new ArrayList<>();
        int urgentCount = 0;
        int delayedCount = 0;
        int completedCount = 0;

        for (Task task : tasks) {
            String warningText = taskService.aiWarning(task);
            // 检查AI警告文本是否为空
            if (warningText == null || warningText.trim().isEmpty()) {
                warningText = "⚠️ 未能生成AI警告，请稍后重试";
            }
            String warningStyle = getWarningStyle(task);

            Map<String, Object> warning = new HashMap<>();
            warning.put("taskId", task.getId());
            warning.put("title", task.getTitle());
            warning.put("status", task.getStatus());
            warning.put("deadline", task.getDeadline());
            warning.put("aiWarning", warningText);
            warning.put("warningStyle", warningStyle);

            warnings.add(warning);

            // 统计各种类型的任务
            if ("urgent_warning".equals(warningStyle) || "overdue".equals(warningStyle)) {
                urgentCount++;
            } else if ("delayed_humorous".equals(warningStyle)) {
                delayedCount++;
            } else if ("completed".equals(warningStyle)) {
                completedCount++;
            }
        }

        // 生成总体提醒摘要
        String summary = generateSummary(urgentCount, delayedCount, completedCount, tasks.size());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalTasks", tasks.size());
        response.put("urgentTasks", urgentCount);
        response.put("delayedTasks", delayedCount);
        response.put("completedTasks", completedCount);
        response.put("summary", summary);
        response.put("aiWarnings", warnings);

        return response;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
package com.example.aitodo.controller;

import com.example.aitodo.entity.TaskLog;
import com.example.aitodo.service.TaskLogService;
import com.example.aitodo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task-logs")
public class TaskLogController {

    private final TaskLogService taskLogService;
    private final TaskService taskService;

    public TaskLogController(TaskLogService taskLogService, TaskService taskService) {
        this.taskLogService = taskLogService;
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
    private boolean hasPermission(Long taskId, Long currentUserId) {
        if (taskId == null || currentUserId == null) {
            return false;
        }
        var task = taskService.getTaskById(taskId);
        return task != null && task.getUserId() != null && task.getUserId().equals(currentUserId);
    }


    @PostMapping
    public ResponseEntity<?> createTaskLog(@RequestBody TaskLog taskLog, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            if (!hasPermission(taskLog.getTaskId(), currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权为此任务创建日志"));
            }

            TaskLog createdLog = taskLogService.createTaskLog(taskLog);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskLogById(@PathVariable Long id, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        TaskLog log = taskLogService.getTaskLogById(id);
        if (log == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Task log not found"));
        }

        if (!hasPermission(log.getTaskId(), currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权访问此任务日志"));
        }

        return ResponseEntity.ok(log);
    }

    @GetMapping
    public ResponseEntity<?> getAllTaskLogs(HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        // 出于安全考虑，不返回所有任务日志
        // 请使用按任务ID或用户ID查询的特定端点
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("出于安全考虑，请使用特定的查询端点"));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getTaskLogsByTaskId(@PathVariable Long taskId, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        if (!hasPermission(taskId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权访问此任务"));
        }

        List<TaskLog> logs = taskLogService.getTaskLogsByTaskId(taskId);
        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskLog(@PathVariable Long id, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户未登录"));
        }

        TaskLog log = taskLogService.getTaskLogById(id);
        if (log == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Task log not found"));
        }

        if (!hasPermission(log.getTaskId(), currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权删除此任务日志"));
        }

        boolean deleted = taskLogService.deleteTaskLog(id);
        if (deleted) {
            return ResponseEntity.ok(createSuccessResponse("Task log deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Task log not found"));
        }
    }

    @GetMapping("/task/{taskId}/total-delay")
    public ResponseEntity<?> getTotalDelayTime(@PathVariable Long taskId, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            if (!hasPermission(taskId, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("无权访问此任务"));
            }

            int totalDelay = taskLogService.getTotalDelayTime(taskId);
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("totalDelayMinutes", totalDelay);
            response.put("totalDelayHours", String.format("%.2f", totalDelay / 60.0));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }



    @GetMapping("/my-average-delay")
    public ResponseEntity<?> getMyAverageDelayTime(HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            double averageDelay = taskLogService.getAverageDelayTime(currentUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", currentUserId);
            response.put("averageDelayMinutes", averageDelay);
            response.put("averageDelayHours", String.format("%.2f", averageDelay / 60.0));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-delay-stats")
    public ResponseEntity<?> getMyDelayStats(HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户未登录"));
            }

            double averageDelay = taskLogService.getAverageDelayTime(currentUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", currentUserId);
            response.put("averageDelayMinutes", averageDelay);
            response.put("averageDelayHours", String.format("%.2f", averageDelay / 60.0));
            response.put("severity", getDelaySeverity(averageDelay));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private String getDelaySeverity(double averageDelayMinutes) {
        if (averageDelayMinutes <= 60) {
            return "轻度拖延 (≤1小时)";
        } else if (averageDelayMinutes <= 240) {
            return "中度拖延 (1-4小时)";
        } else if (averageDelayMinutes <= 720) {
            return "重度拖延 (4-12小时)";
        } else {
            return "严重拖延 (>12小时)";
        }
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
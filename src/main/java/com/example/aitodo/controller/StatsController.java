package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.entity.Task;
import com.example.aitodo.entity.UserStats;
import com.example.aitodo.mapper.UserStatsMapper;
import com.example.aitodo.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计数据控制器
 * 提供排行榜等统计相关接口
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

    private final TaskService taskService;
    private final UserStatsMapper userStatsMapper;

    public StatsController(TaskService taskService, UserStatsMapper userStatsMapper) {
        this.taskService = taskService;
        this.userStatsMapper = userStatsMapper;
    }

    private Long getCurrentUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    @GetMapping("/daily")
    public Result<?> getDailyStats(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) return Result.error(-1, "未登录");

        List<Task> tasks = taskService.getTasksByUserId(userId);
        
        // 生成过去7天的日期
        List<String> dates = new ArrayList<>();
        List<Integer> completedTasks = new ArrayList<>();
        List<Integer> procrastinationCounts = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            dates.add(targetDate.format(formatter));

            // 当日完成的任务
            long completed = tasks.stream()
                    .filter(t -> t.getStatus() != null && t.getStatus() == 1)
                    .filter(t -> {
                        // Assuming completion time is around created/updated or deadline just for stats approximation
                        // Alternatively checking if deadline fell on that day
                        if (t.getDeadline() != null) return t.getDeadline().toLocalDate().equals(targetDate);
                        else if (t.getCreatedAt() != null) return t.getCreatedAt().toLocalDate().equals(targetDate);
                        return false;
                    })
                    .count();
            completedTasks.add((int) completed);

            // 当日拖延的任务
            long delayed = tasks.stream()
                    .filter(t -> t.getStatus() != null && t.getStatus() == 2)
                    .filter(t -> {
                        if (t.getDeadline() != null) return t.getDeadline().toLocalDate().equals(targetDate);
                        // Using createdAt as fallback
                        if (t.getCreatedAt() != null) return t.getCreatedAt().toLocalDate().equals(targetDate);
                        return false;
                    })
                    .count();
            procrastinationCounts.add((int) delayed);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("completedTasks", completedTasks);
        data.put("procrastinationCounts", procrastinationCounts);
        return Result.success(data);
    }

    @GetMapping("/ratio")
    public Result<?> getRatioStats(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) return Result.error(-1, "未登录");

        List<Task> tasks = taskService.getTasksByUserId(userId);
        long completed = tasks.stream().filter(t -> t.getStatus() != null && t.getStatus() == 1).count();
        long procrastinated = tasks.stream().filter(t -> t.getStatus() != null && t.getStatus() == 2).count();
        // 如果没有数据，给个基础默认展示防止报错
        if (completed == 0 && procrastinated == 0) {
            completed = 1; 
            procrastinated = 0;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("completed", completed);
        data.put("procrastinated", procrastinated);
        return Result.success(data);
    }

    @GetMapping("/user")
    public Result<?> getUserStats(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) return Result.error(-1, "未登录");

        UserStats stats = userStatsMapper.selectByUserId(userId);
        Map<String, Object> data = new HashMap<>();
        if (stats != null) {
            data.put("score", stats.getTotalPoints());
            data.put("level", stats.getCurrentLevel());
        } else {
            data.put("score", 0);
            data.put("level", 1);
        }
        return Result.success(data);
    }

    @GetMapping("/achievements")
    public Result<?> getAchievements(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) return Result.error(-1, "未登录");

        UserStats stats = userStatsMapper.selectByUserId(userId);
        List<Map<String, Object>> list = new ArrayList<>();
        
        // 动态根据等级和积分下发成就
        int level = (stats != null && stats.getCurrentLevel() != null) ? stats.getCurrentLevel() : 1;
        int score = (stats != null && stats.getTotalPoints() != null) ? stats.getTotalPoints() : 0;
        
        Map<String, Object> a1 = new HashMap<>();
        a1.put("id", 1);
        a1.put("name", "初来乍到");
        a1.put("description", "创建了你的第一组任务数据");
        a1.put("icon", "🌟");
        list.add(a1);

        if (score >= 20) {
            Map<String, Object> a2 = new HashMap<>();
            a2.put("id", 2);
            a2.put("name", "小试牛刀");
            a2.put("description", "总积分达到 20 分");
            a2.put("icon", "🔥");
            list.add(a2);
        }
        
        if (level >= 3) {
            Map<String, Object> a3 = new HashMap<>();
            a3.put("id", 3);
            a3.put("name", "时间管理者");
            a3.put("description", "等级达到 3 级");
            a3.put("icon", "🏆");
            list.add(a3);
        }

        return Result.success(list);
    }

}
package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.dto.LeaderboardEntryDTO;
import com.example.aitodo.dto.VisualizationStatsDTO;
import com.example.aitodo.service.VisualizationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/visualization")
public class VisualizationController {

    private final VisualizationService visualizationService;

    public VisualizationController(VisualizationService visualizationService) {
        this.visualizationService = visualizationService;
    }

    /**
     * 获取用户总体统计数据
     * GET /visualization/stats/{userId}
     */
    @GetMapping("/stats/{userId}")
    public Result<VisualizationStatsDTO> getOverallStats(@PathVariable Long userId) {
        VisualizationStatsDTO stats = visualizationService.getOverallStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取用户每日统计数据
     * GET /visualization/daily/{userId}?date=2023-01-01
     * 如果date参数为空，默认使用当天日期
     */
    @GetMapping("/daily/{userId}")
    public Result<VisualizationStatsDTO> getDailyStats(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        VisualizationStatsDTO stats = visualizationService.getDailyStats(userId, date);
        return Result.success(stats);
    }

    /**
     * 获取用户每周统计数据
     * GET /visualization/weekly/{userId}?startDate=2023-01-01
     * startDate 应为周一日期，如果为空，默认使用本周周一
     */
    @GetMapping("/weekly/{userId}")
    public Result<VisualizationStatsDTO> getWeeklyStats(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        if (startDate == null) {
            // 计算本周周一
            startDate = LocalDate.now();
            while (startDate.getDayOfWeek().getValue() != 1) { // 1 = Monday
                startDate = startDate.minusDays(1);
            }
        }
        VisualizationStatsDTO stats = visualizationService.getWeeklyStats(userId, startDate);
        return Result.success(stats);
    }

    /**
     * 获取积分排行榜
     * GET /visualization/leaderboard?limit=10
     * limit参数可选，默认10
     */
    @GetMapping("/leaderboard")
    public Result<List<LeaderboardEntryDTO>> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntryDTO> leaderboard = visualizationService.getLeaderboard(limit);
        return Result.success(leaderboard);
    }
}
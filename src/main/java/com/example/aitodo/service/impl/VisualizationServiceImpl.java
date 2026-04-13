package com.example.aitodo.service.impl;

import com.example.aitodo.dto.LeaderboardEntryDTO;
import com.example.aitodo.dto.VisualizationStatsDTO;
import com.example.aitodo.entity.User;
import com.example.aitodo.entity.UserStats;
import com.example.aitodo.mapper.UserMapper;
import com.example.aitodo.mapper.UserPointsMapper;
import com.example.aitodo.mapper.UserStatsMapper;
import com.example.aitodo.service.VisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisualizationServiceImpl implements VisualizationService {

    private final UserStatsMapper userStatsMapper;
    private final UserPointsMapper userPointsMapper;
    private final UserMapper userMapper;

    @Autowired
    public VisualizationServiceImpl(UserStatsMapper userStatsMapper, UserPointsMapper userPointsMapper, UserMapper userMapper) {
        this.userStatsMapper = userStatsMapper;
        this.userPointsMapper = userPointsMapper;
        this.userMapper = userMapper;
    }

    @Override
    public VisualizationStatsDTO getOverallStats(Long userId) {
        // 从 user_stats 表获取总数据
        var userStats = userStatsMapper.selectByUserId(userId);
        if (userStats == null) {
            // 如果用户没有统计记录，返回默认值
            return new VisualizationStatsDTO(0, 0, 0, 1);
        }
        return new VisualizationStatsDTO(
                userStats.getTotalTasksCompleted(),
                userStats.getTotalTasksDelayed(),
                userStats.getTotalPoints(),
                userStats.getCurrentLevel()
        );
    }

    @Override
    public VisualizationStatsDTO getDailyStats(Long userId, LocalDate date) {
        // 计算日期范围的开始和结束
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        // 查询该日期范围内的完成任务数（reason前缀为"完成任务奖励"）
        Integer completedTasks = userPointsMapper.countByUserIdReasonPrefixAndDateRange(
                userId, "完成任务奖励", start, end);

        // 查询该日期范围内的拖延任务数（reason前缀为"任务拖延扣除"）
        Integer delayedTasks = userPointsMapper.countByUserIdReasonPrefixAndDateRange(
                userId, "任务拖延扣除", start, end);

        // 查询该日期范围内的积分变化总和
        Integer pointsChange = userPointsMapper.sumPointsByUserId(userId);
        // 注意：这里返回的是总积分，不是当日积分变化。如果需要当日积分变化，可以计算：
        // Integer dailyPointsChange = userPointsMapper.sumPointsByUserIdAndDateRange(userId, start, end);
        // 但当前需求可能想要总积分，所以我们从user_stats获取总积分
        var userStats = userStatsMapper.selectByUserId(userId);
        Integer totalPoints = userStats != null ? userStats.getTotalPoints() : 0;
        Integer currentLevel = userStats != null ? userStats.getCurrentLevel() : 1;

        return new VisualizationStatsDTO(completedTasks, delayedTasks, totalPoints, currentLevel);
    }

    @Override
    public VisualizationStatsDTO getWeeklyStats(Long userId, LocalDate startOfWeek) {
        // 假设 startOfWeek 是周一，计算周日结束
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = endOfWeek.plusDays(1).atStartOfDay(); // 下一天的零点

        Integer completedTasks = userPointsMapper.countByUserIdReasonPrefixAndDateRange(
                userId, "完成任务奖励", start, end);
        Integer delayedTasks = userPointsMapper.countByUserIdReasonPrefixAndDateRange(
                userId, "任务拖延扣除", start, end);

        var userStats = userStatsMapper.selectByUserId(userId);
        Integer totalPoints = userStats != null ? userStats.getTotalPoints() : 0;
        Integer currentLevel = userStats != null ? userStats.getCurrentLevel() : 1;

        return new VisualizationStatsDTO(completedTasks, delayedTasks, totalPoints, currentLevel);
    }

    @Override
    public List<LeaderboardEntryDTO> getLeaderboard(int limit) {
        // 使用JOIN查询获取排行榜数据（包含用户名和排名）
        return userStatsMapper.selectLeaderboard(limit);
    }
}
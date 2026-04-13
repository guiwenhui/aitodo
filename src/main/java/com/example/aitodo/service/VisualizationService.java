package com.example.aitodo.service;

import com.example.aitodo.dto.LeaderboardEntryDTO;
import com.example.aitodo.dto.VisualizationStatsDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * 可视化数据服务接口
 */
public interface VisualizationService {

    /**
     * 获取用户总体统计数据（总完成任务数、总拖延任务数、总积分、当前等级）
     * @param userId 用户ID
     * @return 统计数据DTO
     */
    VisualizationStatsDTO getOverallStats(Long userId);

    /**
     * 获取用户每日统计数据（指定日期的完成任务数、拖延任务数、积分变化、等级）
     * 积分和等级返回当日的总值（截至该日期）
     * @param userId 用户ID
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 统计数据DTO
     */
    VisualizationStatsDTO getDailyStats(Long userId, LocalDate date);

    /**
     * 获取用户每周统计数据（指定周的开始日期，周一到周日）
     * @param userId 用户ID
     * @param startOfWeek 周起始日期（周一）
     * @return 统计数据DTO
     */
    VisualizationStatsDTO getWeeklyStats(Long userId, LocalDate startOfWeek);

    /**
     * 获取积分排行榜
     * @param limit 返回前N名用户
     * @return 排行榜条目列表
     */
    List<LeaderboardEntryDTO> getLeaderboard(int limit);
}
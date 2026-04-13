package com.example.aitodo.service;

import com.example.aitodo.dto.LeaderboardEntryDTO;
import java.util.List;

/**
 * 排行榜服务接口
 */
public interface LeaderboardService {

    /**
     * 获取积分排行榜
     * @param limit 限制返回数量，如果为0则返回所有
     * @return 排行榜条目列表，按积分降序排序
     */
    List<LeaderboardEntryDTO> getLeaderboard(int limit);

    /**
     * 获取完整积分排行榜
     * @return 所有用户的排行榜条目，按积分降序排序
     */
    List<LeaderboardEntryDTO> getFullLeaderboard();
}
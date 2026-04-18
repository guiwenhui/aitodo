package com.example.aitodo.service.impl;

import com.example.aitodo.dto.LeaderboardEntryDTO;
import com.example.aitodo.mapper.UserStatsMapper;
import com.example.aitodo.service.LeaderboardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final UserStatsMapper userStatsMapper;

    public LeaderboardServiceImpl(UserStatsMapper userStatsMapper) {
        this.userStatsMapper = userStatsMapper;
    }

    @Override
    public List<LeaderboardEntryDTO> getLeaderboard(int limit) {
        // 1. 获取所有用户基础数据 (这条 SQL 我们之前加了 avatar_url，记得确认有)
        List<LeaderboardEntryDTO> list = userStatsMapper.selectAllLeaderboard();

        // 2. 🌟 Java 后端暴力双重排序！
        list.sort((a, b) -> {
            int levelA = a.getLevel() != null ? a.getLevel() : 0;
            int levelB = b.getLevel() != null ? b.getLevel() : 0;

            // 优先看等级 (降序)
            if (levelA != levelB) {
                return Integer.compare(levelB, levelA);
            }
            // 等级相同看积分 (降序)
            int pointsA = a.getPoints() != null ? a.getPoints() : 0;
            int pointsB = b.getPoints() != null ? b.getPoints() : 0;
            return Integer.compare(pointsB, pointsA);
        });

        // 3. 强制重写名次！(彻底击碎 SQL 里算错的 Rank)
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }

        // 4. 截取前 N 名
        if (limit > 0 && list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    @Override
    public List<LeaderboardEntryDTO> getFullLeaderboard() {
        return getLeaderboard(0);
    }
}
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
        // 1. 无视 SQL 里面算出来的乱七八糟的名次，把所有人的基础数据查出来
        List<LeaderboardEntryDTO> list = userStatsMapper.selectAllLeaderboard();

        // 2. 🌟 绝对防御：在 Java 内存中强制接管排序生杀大权！
        // 规则：优先比等级 (降序)，等级一样比积分 (降序)
        list.sort((a, b) -> {
            int levelA = a.getLevel() != null ? a.getLevel() : 0;
            int levelB = b.getLevel() != null ? b.getLevel() : 0;

            // 优先看等级，等级高的排前面
            if (levelA != levelB) {
                return Integer.compare(levelB, levelA);
            }
            // 如果等级一模一样，再看积分，积分高的排前面
            int pointsA = a.getPoints() != null ? a.getPoints() : 0;
            int pointsB = b.getPoints() != null ? b.getPoints() : 0;
            return Integer.compare(pointsB, pointsA);
        });

        // 3. 🌟 重新发奖杯：不管以前排第几，现在强制按数组位置重新发名次！第一名就是 1！
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }

        // 4. 处理前端需要的截取（比如只要前10名）
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
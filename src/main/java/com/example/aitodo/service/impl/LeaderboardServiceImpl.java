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
        if (limit <= 0) {
            return userStatsMapper.selectAllLeaderboard();
        }
        return userStatsMapper.selectLeaderboard(limit);
    }

    @Override
    public List<LeaderboardEntryDTO> getFullLeaderboard() {
        return userStatsMapper.selectAllLeaderboard();
    }
}
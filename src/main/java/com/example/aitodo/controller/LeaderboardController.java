package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.dto.LeaderboardEntryDTO;
import com.example.aitodo.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排行榜控制器
 * 提供积分排行榜相关接口
 */
@RestController
@RequestMapping("/stats")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * 获取积分排行榜
     * @param limit 限制返回数量，可选，默认为0表示返回所有用户
     * @return 排行榜数据
     */
    @GetMapping("/leaderboard")
    public Result getLeaderboard(@RequestParam(value = "limit", defaultValue = "0") int limit) {
        List<LeaderboardEntryDTO> leaderboard = leaderboardService.getLeaderboard(limit);
        return Result.success(leaderboard);
    }

    /**
     * 获取完整排行榜（所有用户）
     * @return 完整排行榜数据
     */
    @GetMapping("/leaderboard/full")
    public Result getFullLeaderboard() {
        List<LeaderboardEntryDTO> leaderboard = leaderboardService.getFullLeaderboard();
        return Result.success(leaderboard);
    }
}
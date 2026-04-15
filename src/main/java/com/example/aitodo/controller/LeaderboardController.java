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

        @GetMapping("/leaderboard")
        public Result<?> getLeaderboard() {
            // 🌟 绝杀：必须确保这里调用的是 getLeaderboard，而不是其他的旧方法！
            return Result.success(leaderboardService.getLeaderboard(10));
        }
}
package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    public Result<?> getLeaderboard() {
        return Result.success(leaderboardService.getLeaderboard(10));
    }
}
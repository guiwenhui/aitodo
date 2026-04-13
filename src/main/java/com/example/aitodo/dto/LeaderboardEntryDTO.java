package com.example.aitodo.dto;

/**
 * 排行榜条目数据传输对象
 */
public class LeaderboardEntryDTO {
    private Long userId;
    private String username;  // 需要从User表获取，这里先留空或通过JOIN查询
    private Integer points;
    private Integer level;
    private Integer rank;

    public LeaderboardEntryDTO() {
    }

    public LeaderboardEntryDTO(Long userId, String username, Integer points, Integer level, Integer rank) {
        this.userId = userId;
        this.username = username;
        this.points = points;
        this.level = level;
        this.rank = rank;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "LeaderboardEntryDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", points=" + points +
                ", level=" + level +
                ", rank=" + rank +
                '}';
    }
}
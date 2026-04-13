package com.example.aitodo.mapper;

import com.example.aitodo.entity.UserStats;
import com.example.aitodo.dto.LeaderboardEntryDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserStatsMapper {

    @Insert("INSERT INTO user_stats(user_id, total_tasks_completed, total_tasks_delayed, " +
            "total_points, current_level, last_updated, created_at) " +
            "VALUES(#{userId}, #{totalTasksCompleted}, #{totalTasksDelayed}, " +
            "#{totalPoints}, #{currentLevel}, #{lastUpdated}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserStats userStats);

    @Update("UPDATE user_stats SET " +
            "total_tasks_completed = #{totalTasksCompleted}, " +
            "total_tasks_delayed = #{totalTasksDelayed}, " +
            "total_points = #{totalPoints}, " +
            "current_level = #{currentLevel}, " +
            "last_updated = #{lastUpdated} " +
            "WHERE id = #{id}")
    int update(UserStats userStats);

    @Update("UPDATE user_stats SET " +
            "total_tasks_completed = total_tasks_completed + #{increment}, " +
            "last_updated = NOW() " +
            "WHERE user_id = #{userId}")
    int incrementTasksCompleted(@Param("userId") Long userId, @Param("increment") int increment);

    @Update("UPDATE user_stats SET " +
            "total_tasks_delayed = total_tasks_delayed + #{increment}, " +
            "last_updated = NOW() " +
            "WHERE user_id = #{userId}")
    int incrementTasksDelayed(@Param("userId") Long userId, @Param("increment") int increment);

    @Update("UPDATE user_stats SET " +
            "total_points = total_points + #{points}, " +
            "last_updated = NOW() " +
            "WHERE user_id = #{userId}")
    int addPoints(@Param("userId") Long userId, @Param("points") int points);

    @Update("UPDATE user_stats SET " +
            "current_level = #{level}, " +
            "last_updated = NOW() " +
            "WHERE user_id = #{userId}")
    int updateLevel(@Param("userId") Long userId, @Param("level") int level);

    @Select("SELECT * FROM user_stats WHERE id = #{id}")
    UserStats selectById(Long id);

    @Select("SELECT * FROM user_stats WHERE user_id = #{userId}")
    UserStats selectByUserId(Long userId);

    @Select("SELECT * FROM user_stats")
    List<UserStats> selectAll();

    @Select("SELECT COUNT(*) FROM user_stats WHERE user_id = #{userId}")
    int existsByUserId(Long userId);

    @Insert("INSERT INTO user_stats(user_id, total_tasks_completed, total_tasks_delayed, " +
            "total_points, current_level) " +
            "VALUES(#{userId}, 0, 0, 0, 1)")
    int initUserStats(Long userId);

    // 获取用户排名（按积分降序）
    @Select("SELECT *, " +
            "(SELECT COUNT(*) FROM user_stats us2 WHERE us2.total_points > us1.total_points) + 1 AS rank " +
            "FROM user_stats us1 WHERE user_id = #{userId}")
    UserStats selectWithRank(Long userId);

    // 获取前N名用户
    @Select("SELECT * FROM user_stats ORDER BY total_points DESC LIMIT #{limit}")
    List<UserStats> selectTopUsers(@Param("limit") int limit);

    @Select("SELECT us.user_id AS userId, u.username, us.total_points AS points, us.current_level AS level, " +
            "(SELECT COUNT(*) FROM user_stats us2 WHERE us2.total_points > us.total_points) + 1 AS `rank` " +
            "FROM user_stats us " +
            "LEFT JOIN users u ON us.user_id = u.id " +
            "ORDER BY us.total_points DESC LIMIT #{limit}")
    List<LeaderboardEntryDTO> selectLeaderboard(@Param("limit") int limit);

    @Select("SELECT us.user_id AS userId, u.username, us.total_points AS points, us.current_level AS level, " +
            "(SELECT COUNT(*) FROM user_stats us2 WHERE us2.total_points > us.total_points) + 1 AS `rank` " +
            "FROM user_stats us " +
            "LEFT JOIN users u ON us.user_id = u.id " +
            "ORDER BY us.total_points DESC")
    List<LeaderboardEntryDTO> selectAllLeaderboard();
}
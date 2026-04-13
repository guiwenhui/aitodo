package com.example.aitodo.mapper;

import com.example.aitodo.entity.UserPoints;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserPointsMapper {

    @Insert("INSERT INTO user_points(user_id, points_change, reason, task_id, created_at) " +
            "VALUES(#{userId}, #{pointsChange}, #{reason}, #{taskId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserPoints userPoints);

    @Select("SELECT * FROM user_points WHERE id = #{id}")
    UserPoints selectById(Long id);

    @Select("SELECT * FROM user_points WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserPoints> selectByUserId(Long userId);

    @Select("SELECT * FROM user_points WHERE user_id = #{userId} AND reason LIKE CONCAT(#{reasonPrefix}, '%')")
    List<UserPoints> selectByUserIdAndReasonPrefix(@Param("userId") Long userId, @Param("reasonPrefix") String reasonPrefix);

    @Select("SELECT * FROM user_points WHERE user_id = #{userId} AND created_at >= #{start} AND created_at < #{end}")
    List<UserPoints> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Select("SELECT * FROM user_points WHERE user_id = #{userId} AND reason LIKE CONCAT(#{reasonPrefix}, '%') " +
            "AND created_at >= #{start} AND created_at < #{end}")
    List<UserPoints> selectByUserIdReasonPrefixAndDateRange(@Param("userId") Long userId,
                                                             @Param("reasonPrefix") String reasonPrefix,
                                                             @Param("start") LocalDateTime start,
                                                             @Param("end") LocalDateTime end);

    @Select("SELECT COALESCE(SUM(points_change), 0) FROM user_points WHERE user_id = #{userId} AND reason LIKE CONCAT(#{reasonPrefix}, '%')")
    Integer sumPointsByUserIdAndReasonPrefix(@Param("userId") Long userId, @Param("reasonPrefix") String reasonPrefix);

    @Select("SELECT COALESCE(SUM(points_change), 0) FROM user_points WHERE user_id = #{userId}")
    Integer sumPointsByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM user_points WHERE user_id = #{userId} AND reason LIKE CONCAT(#{reasonPrefix}, '%')")
    Integer countByUserIdAndReasonPrefix(@Param("userId") Long userId, @Param("reasonPrefix") String reasonPrefix);

    @Select("SELECT COUNT(*) FROM user_points WHERE user_id = #{userId} AND reason LIKE CONCAT(#{reasonPrefix}, '%') " +
            "AND created_at >= #{start} AND created_at < #{end}")
    Integer countByUserIdReasonPrefixAndDateRange(@Param("userId") Long userId,
                                                   @Param("reasonPrefix") String reasonPrefix,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    @Select("SELECT COALESCE(SUM(points_change), 0) FROM user_points WHERE user_id = #{userId} " +
            "AND created_at >= #{start} AND created_at < #{end}")
    Integer sumPointsByUserIdAndDateRange(@Param("userId") Long userId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}
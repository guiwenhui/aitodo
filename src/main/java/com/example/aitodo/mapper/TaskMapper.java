package com.example.aitodo.mapper;

import com.example.aitodo.entity.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {

    @Insert("INSERT INTO tasks(user_id, title, deadline, status, created_at) " +
            "VALUES(#{userId}, #{title}, #{deadline}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);

    @Update("UPDATE tasks SET user_id=#{userId}, title=#{title}, deadline=#{deadline}, " +
            "status=#{status}, created_at=#{createdAt} WHERE id=#{id}")
    int update(Task task);

    @Delete("DELETE FROM tasks WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM tasks WHERE id=#{id}")
    Task selectById(Long id);

    @Select("SELECT * FROM tasks")
    List<Task> selectAll();

    @Select("SELECT * FROM tasks WHERE user_id=#{userId}")
    List<Task> selectByUserId(Long userId);

    @Select("SELECT * FROM tasks WHERE status=#{status}")
    List<Task> selectByStatus(Integer status);

    @Select("SELECT * FROM tasks WHERE deadline < NOW() AND status = 0")
    List<Task> selectOverdueTasks();
}
package com.example.aitodo.mapper;

import com.example.aitodo.entity.TaskLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskLogMapper {

    @Insert("INSERT INTO task_logs(task_id, delay_time) VALUES(#{taskId}, #{delayTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskLog taskLog);

    @Update("UPDATE task_logs SET task_id=#{taskId}, delay_time=#{delayTime} WHERE id=#{id}")
    int update(TaskLog taskLog);

    @Delete("DELETE FROM task_logs WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM task_logs WHERE id=#{id}")
    TaskLog selectById(Long id);

    @Select("SELECT * FROM task_logs")
    List<TaskLog> selectAll();

    @Select("SELECT * FROM task_logs WHERE task_id=#{taskId}")
    List<TaskLog> selectByTaskId(Long taskId);
}
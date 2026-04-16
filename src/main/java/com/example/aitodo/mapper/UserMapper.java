package com.example.aitodo.mapper;

import com.example.aitodo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users(username, password, created_at) VALUES(#{username}, #{password}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET username=#{username}, password=#{password} WHERE id=#{id}")
    int update(User user);

    @Delete("DELETE FROM users WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM users WHERE id=#{id}")
    User selectById(Long id);

    @Select("SELECT * FROM users")
    List<User> selectAll();

    @Select("SELECT * FROM users WHERE username=#{username}")
    User selectByUsername(String username);

    @Update("UPDATE users SET avatar_url=#{avatarUrl} WHERE id=#{id}")
    int updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);
}
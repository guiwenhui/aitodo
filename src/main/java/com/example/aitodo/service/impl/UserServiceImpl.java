package com.example.aitodo.service.impl;

import com.example.aitodo.entity.User;
import com.example.aitodo.mapper.UserMapper;
import com.example.aitodo.mapper.UserStatsMapper;
import com.example.aitodo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserStatsMapper userStatsMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, UserStatsMapper userStatsMapper) {
        this.userMapper = userMapper;
        this.userStatsMapper = userStatsMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User register(User user) {
        // Check if username already exists
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        // Set creation timestamp
        user.setCreatedAt(LocalDateTime.now());

        // Encrypt password before storing
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // Insert user
        userMapper.insert(user);

        // Initialize user stats for the new user
        userStatsMapper.initUserStats(user.getId());

        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }

        String storedPassword = user.getPassword();

        // 检查密码是否匹配（支持BCrypt哈希和明文迁移）
        if (passwordEncoder.matches(password, storedPassword)) {
            return user;
        }

        // 如果BCrypt不匹配，检查是否是明文密码（用于迁移）
        if (password.equals(storedPassword)) {
            // 迁移到BCrypt
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            userMapper.update(user);
            return user;
        }

        return null;
    }

    @Override
    public User updateUser(User user) {
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }

        // Check if username changed and new username exists
        if (!existing.getUsername().equals(user.getUsername())) {
            User userWithSameName = userMapper.selectByUsername(user.getUsername());
            if (userWithSameName != null) {
                throw new RuntimeException("Username already exists: " + user.getUsername());
            }
        }

        userMapper.update(user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }
        userMapper.deleteById(id);
        return true;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    @Override
    public void updateAvatarUrl(Long userId, String avatarUrl) {
        userMapper.updateAvatarUrl(userId, avatarUrl);
    }
}
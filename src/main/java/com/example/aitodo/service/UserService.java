package com.example.aitodo.service;

import com.example.aitodo.entity.User;
import java.util.List;

public interface UserService {

    /**
     * Register a new user
     * @param user user object
     * @return registered user with id
     */
    User register(User user);

    /**
     * User login
     * @param username username
     * @param password password
     * @return user object if login success, null otherwise
     */
    User login(String username, String password);

    /**
     * Update user information
     * @param user user object
     * @return updated user
     */
    User updateUser(User user);

    /**
     * Delete user by id
     * @param id user id
     * @return true if deleted successfully
     */
    boolean deleteUser(Long id);

    /**
     * Get user by id
     * @param id user id
     * @return user object
     */
    User getUserById(Long id);

    /**
     * Get all users
     *
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Check if username exists
     * @param username username
     * @return true if exists
     */
    boolean checkUsernameExists(String username);

    /**
     * Update user avatar URL
     * @param userId user id
     * @param avatarUrl new avatar URL
     */
    void updateAvatarUrl(Long userId, String avatarUrl);
}
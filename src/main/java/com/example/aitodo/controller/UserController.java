package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.entity.User;
import com.example.aitodo.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            // 不返回密码
            registeredUser.setPassword(null);
            return Result.success(registeredUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return Result.error("Username and password are required");
        }

        User user = userService.login(username, password);
        if (user == null) {
            return Result.error(401, "Invalid username or password");
        }

        // 清除密码后再存入session
        user.setPassword(null);
        // 设置session，存储整个user对象
        session.setAttribute("user", user);
        // 同时存储userId和username，方便其他控制器使用
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        // 存储头像URL到session
        session.setAttribute("avatarUrl", user.getAvatarUrl());

        // 返回登录成功消息和用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("redirectUrl", "/");
        return Result.success("login success", data);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.success("Logged out successfully");
    }

    /**
     * 用户上传头像
     * 从 Session 中获取当前用户 ID，接收 MultipartFile，
     * 使用 UUID 生成唯一文件名，保存到本地 uploads/avatars/ 目录，
     * 更新数据库和 Session 中的 avatarUrl。
     */
    @PostMapping({"/uploadAvatar", "/api/avatar"})
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 1. 验证用户登录状态
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        // 2. 验证文件
        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        // 3. 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只允许上传图片文件");
        }

        try {
            // 4. 获取原文件的后缀名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 5. UUID 生成唯一文件名
            String newFileName = UUID.randomUUID().toString() + extension;

            // 6. 定义保存路径（项目根目录/uploads/avatars/）
            String projectRoot = System.getProperty("user.dir");
            String saveDirPath = projectRoot + "/uploads/avatars/";
            File saveDir = new File(saveDirPath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 7. 写入磁盘
            File dest = new File(saveDirPath + newFileName);
            file.transferTo(dest);

            // 8. 生成虚拟访问路径（与 WebMvcConfig 中的映射对应）
            String avatarUrl = "/uploads/avatars/" + newFileName;

            // 9. 更新数据库
            userService.updateAvatarUrl(userId, avatarUrl);

            // 10. 更新 Session（确保 Thymeleaf 后续渲染正确）
            session.setAttribute("avatarUrl", avatarUrl);

            // 11. 返回新的 avatarUrl
            Map<String, String> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            return Result.success("头像更新成功", data);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败，服务器发生异常");
        }
    }
}
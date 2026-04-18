package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.entity.User;
import com.example.aitodo.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 👇 新增：注入和 WebMvcConfig 一模一样的路径配置
    @Value("${app.upload.path:./uploads/}")
    private String uploadPath;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
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

        user.setPassword(null);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("avatarUrl", user.getAvatarUrl());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("redirectUrl", "/");
        return Result.success("login success", data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.success("Logged out successfully");
    }

    @PostMapping({"/uploadAvatar", "/api/avatar"})
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只允许上传图片文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID().toString() + extension;

            // 👇 修改重点：使用统一的绝对路径，杜绝路径漂移！
            String absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize().toString();
            String saveDirPath = absolutePath + "/avatars/";

            File saveDir = new File(saveDirPath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            File dest = new File(saveDirPath + newFileName);
            file.transferTo(dest);

            String avatarUrl = "/uploads/avatars/" + newFileName;

            userService.updateAvatarUrl(userId, avatarUrl);
            session.setAttribute("avatarUrl", avatarUrl);

            Map<String, String> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            return Result.success("头像更新成功", data);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败，服务器发生异常");
        }
    }
}
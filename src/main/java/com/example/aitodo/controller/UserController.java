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

        // 返回登录成功消息和用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
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
     */
    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        try {
            // 1. 获取原文件的后缀名 (比如 .jpg, .png)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 2. 为了防止同名文件覆盖，用 UUID 生成一个世界上独一无二的新文件名
            String newFileName = UUID.randomUUID().toString() + extension;

            // 3. 定义保存路径（绝对路径，指向我们刚才在 Linux 上建的文件夹）
            String savePath = "/opt/aitodo/avatars/" + newFileName;

            // 4. 将前端传来的文件写入硬盘！
            File dest = new File(savePath);
            file.transferTo(dest);

            // 5. 拼装出这图片未来的网络访问 URL (注意替换成你的公网 IP)
            String avatarUrl = "http://47.109.156.71/avatars/" + newFileName;

            // 6. 调用 Service，把这个 URL 更新到数据库中该用户的记录里
            // userService.updateAvatarUrl(userId, avatarUrl);

            return "头像更新成功！新的URL是: " + avatarUrl;

        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败，服务器发生异常";
        }
    }
}
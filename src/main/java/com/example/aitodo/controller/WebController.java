package com.example.aitodo.controller;

import com.example.aitodo.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

/**
 * 前端页面控制器
 * 提供 Thymeleaf 模板页面
 */
@Controller
public class WebController {

    private final TaskService taskService;

    public WebController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 首页 - 根据登录状态重定向
     */
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            // 已登录，显示 vue-home
            return "vue-home";
        } else {
            // 未登录，显示首页（宣传页面）
            model.addAttribute("appName", "AI Todo + 拖延症治疗器");
            model.addAttribute("version", "1.0.0");
            return "index";
        }
    }

    /**
     * 任务管理页面
     */
    @GetMapping("/tasks")
    public String tasks(Model model, HttpSession session) {
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        if (userId == null) {
            // 未登录，重定向到登录页面
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "任务管理 - AI Todo");
        // 获取当前登录用户的任务列表
        var tasks = taskService.getTasksByUserId(userId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("aiWarning", "⏰ 你有1个紧急任务需要立即处理！拖延只会让问题变大，现在就开始行动吧！");
        model.addAttribute("loggedIn", true);
        model.addAttribute("username", username);
        return "tasks";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("pageTitle", "用户管理 - AI Todo");
        return "users";
    }

    /**
     * AI 提醒页面
     */
    @GetMapping("/ai-warnings")
    public String aiWarnings(Model model, HttpSession session) {
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        if (userId == null) {
            // 未登录，重定向到登录页面
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "AI 提醒 - 拖延症治疗器");
        model.addAttribute("loggedIn", true);
        model.addAttribute("username", username);
        return "ai-warnings";
    }

    /**
     * Vue.js 简约首页
     */
    @GetMapping("/vue-home")
    public String vueHome() {
        return "vue-home";
    }

    /**
     * 积分排行榜 / 数据大屏 (Vue项目入口)
     */
    @GetMapping("/leaderboard")
    public String leaderboard(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        return "forward:/vue/index.html";
    }
    /**
     * 健康检查页面
     */
    @GetMapping("/health")
    public String healthPage(Model model) {
        model.addAttribute("appName", "AI Todo + 拖延症治疗器");
        model.addAttribute("version", "1.0.0");
        return "health";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        // 如果用户已登录，重定向到任务页面
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            return "redirect:/tasks";
        }

        model.addAttribute("appName", "AI Todo + 拖延症治疗器");
        model.addAttribute("version", "1.0.0");
        return "login";
    }

    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        // 如果用户已登录，重定向到任务页面
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            return "redirect:/tasks";
        }

        model.addAttribute("appName", "AI Todo + 拖延症治疗器");
        model.addAttribute("version", "1.0.0");
        return "register";
    }

    /**
     * 用户退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
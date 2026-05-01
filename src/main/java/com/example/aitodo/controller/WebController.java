package com.example.aitodo.controller;

import com.example.aitodo.entity.User;
import com.example.aitodo.service.TaskService;
import com.example.aitodo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

/**
 * 前端页面控制器
 * 全面拥抱纯 Vue SPA 架构，所有前端路由均转发给 Vue 的 index.html 处理。
 */
@Controller
public class WebController {

    /**
     * 捕获所有的前端页面路由并转发到 Vue 的入口 index.html
     */
    @GetMapping({
            "/",
            "/tasks",
            "/users",
            "/ai-warnings",
            "/vue-home",
            "/leaderboard",
            "/health",
            "/login",
            "/register",
            "/focus-flow"
    })
    public String forwardToVue() {
        return "forward:/index.html";
    }

    /**
     * 捕获带参数或深层的特定前端路由 (原为 /vue/* 的 Catch-All)
     */
    @GetMapping("/vue/{path:[^\\.]*}")
    public String vueSpaForward() {
        return "redirect:/"; // 旧的 /vue/ 路由重定向到根目录
    }
}
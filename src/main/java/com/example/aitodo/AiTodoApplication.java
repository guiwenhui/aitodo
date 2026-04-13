package com.example.aitodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.aitodo.mapper")
public class AiTodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiTodoApplication.class, args);
    }

}
package com.example.oreohack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.example.oreohack")
public class OreoHackApplication {

    public static void main(String[] args) {
        SpringApplication.run(OreoHackApplication.class, args);
    }

}

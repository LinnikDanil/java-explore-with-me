package ru.practicum.explore_with_me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.stats_client", "ru.practicum.explore_with_me"})
public class ExploreWithMeServer {
    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMeServer.class, args);
    }
}

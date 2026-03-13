package com.batrobot.bot.infrastructure.config;

import com.pengrad.telegrambot.TelegramBot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    private final TelegramBotProperties properties;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(properties.getToken());
    }
}

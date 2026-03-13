package com.batrobot.chat.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batrobot.chat.domain.repository.ChatRepository;
import com.batrobot.chat.domain.specification.UniqueChatSpecification;

@Configuration
public class ChatSpecificationConfiguration {

    @Bean
    public UniqueChatSpecification uniqueChatSpecification(
            ChatRepository repository) {
        return new UniqueChatSpecification(repository);
    }
}


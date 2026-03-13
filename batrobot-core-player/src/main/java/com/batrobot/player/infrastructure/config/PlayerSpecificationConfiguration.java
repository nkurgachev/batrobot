package com.batrobot.player.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.player.domain.specification.UniquePlayerSpecification;

@Configuration
public class PlayerSpecificationConfiguration {
    @Bean
    public UniquePlayerSpecification uniquePlayerSpecification(
            PlayerRepository repository) {
        return new UniquePlayerSpecification(repository);
    }
}


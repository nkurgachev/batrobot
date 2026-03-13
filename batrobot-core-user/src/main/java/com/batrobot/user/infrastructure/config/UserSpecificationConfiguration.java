package com.batrobot.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batrobot.user.domain.repository.UserRepository;
import com.batrobot.user.domain.specification.UniqueUserSpecification;

@Configuration
public class UserSpecificationConfiguration {
    @Bean
    public UniqueUserSpecification uniqueUserSpecification(
            UserRepository repository) {
        return new UniqueUserSpecification(repository);
    }
}

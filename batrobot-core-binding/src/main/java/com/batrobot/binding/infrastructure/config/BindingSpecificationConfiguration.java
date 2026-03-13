package com.batrobot.binding.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.binding.domain.specification.UniquePlayerBindingSpecification;

@Configuration
public class BindingSpecificationConfiguration {
    
    @Bean
    public UniquePlayerBindingSpecification uniquePlayerBindingSpecification(
            ChatPlayerBindingRepository repository) {
        return new UniquePlayerBindingSpecification(repository);
    }
}


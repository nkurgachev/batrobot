package com.batrobot.shared.infrastructure.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batrobot.shared.application.event.DomainEventPublisher;
import com.batrobot.shared.infrastructure.event.DomainEventPublisherImpl;

@Configuration
public class BatRobotSharedAutoConfiguration {
    
    @Bean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher eventPublisher) {
        return new DomainEventPublisherImpl(eventPublisher);
    }
}

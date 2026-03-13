package com.batrobot.shared.infrastructure.persistence.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.batrobot.shared.application.event.DomainEventPublisher;
import com.batrobot.shared.domain.model.BaseAggregateRoot;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DomainEventPublishingAspect {

    private final DomainEventPublisher eventPublisher;

    @AfterReturning(pointcut = "execution(* com.batrobot..*Repository.save(..)) && args(aggregate)", argNames = "aggregate")
    public void publishEvents(Object aggregate) {
        if (aggregate instanceof BaseAggregateRoot root) {
            root.getDomainEvents().forEach(eventPublisher::publish);
            root.clearDomainEvents();
        }
    }

    @AfterReturning(pointcut = "execution(* com.batrobot..*Repository.saveAll(..)) && args(entities)", argNames = "entities")
    public void publishEventsFromCollection(Object entities) {
        if (entities instanceof Iterable<?> iterable) {
            for (Object entity : iterable) {
                if (entity instanceof BaseAggregateRoot root) {
                    root.getDomainEvents().forEach(eventPublisher::publish);
                    root.clearDomainEvents();
                }
            }
        }
    }
}

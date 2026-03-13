package com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Annotation for marking command handler components
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CommandHandlerComponent {
    String value();
}

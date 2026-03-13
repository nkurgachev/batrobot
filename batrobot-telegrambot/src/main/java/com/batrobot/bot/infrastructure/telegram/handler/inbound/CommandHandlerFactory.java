package com.batrobot.bot.infrastructure.telegram.handler.inbound;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating and managing command handlers
 */
@Component
public class CommandHandlerFactory {
    
    private final Map<String, CommandHandler> handlers = new ConcurrentHashMap<>();
    
    private CommandHandlerFactory(ListableBeanFactory beanFactory) {
        Map<String, Object> beans = beanFactory.getBeansWithAnnotation(CommandHandlerComponent.class);
        
        beans.forEach((beanName, bean) -> {
            if (bean instanceof CommandHandler handler) {
                CommandHandlerComponent annotation = beanFactory.findAnnotationOnBean(beanName, CommandHandlerComponent.class);
                if (annotation != null) {
                    handlers.put(annotation.value(), handler);
                }
            }
        });
    }
    
    public Optional<CommandHandler> getHandler(String commandName) {
        return Optional.ofNullable(handlers.get(commandName));
    }
}

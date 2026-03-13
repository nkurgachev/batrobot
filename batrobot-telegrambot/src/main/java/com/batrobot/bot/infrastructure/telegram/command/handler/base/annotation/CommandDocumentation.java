package com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation;

import java.lang.annotation.*;

/** 
 * Annotation for documenting Telegram bot commands 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDocumentation {
    String descriptionKey();
    String usage() default "";
    String example() default "";
    String noteKey() default "";
}

package com.batrobot.orchestration.application.dto.request;

import com.batrobot.orchestration.application.dto.request.info.ChatInfo;
import com.batrobot.orchestration.application.dto.request.info.UserInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Request DTO for set_emoji command.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetEmojiCommandRequest {

    @Valid
    @NotNull(message = "Chat info cannot be null")
    private ChatInfo chat;

    @Valid
    @NotNull(message = "User info cannot be null")
    private UserInfo user;

    @NotBlank(message = "Emoji cannot be blank")
    private String emoji;
}

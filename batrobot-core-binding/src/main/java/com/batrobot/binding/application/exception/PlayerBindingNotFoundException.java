package com.batrobot.binding.application.exception;

import com.batrobot.shared.application.exception.ApplicationException;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import lombok.Getter;

/**
 * Thrown when a player binding is not found.
 */
@Getter
public class PlayerBindingNotFoundException extends ApplicationException {

    private final SteamId steamId;
    private final TelegramUserId telegramUserId;

    public PlayerBindingNotFoundException(SteamId steamId, TelegramUserId telegramUserId) {
        super("Player binding not found: " + steamId + ", userId: " + telegramUserId);
        this.steamId = steamId;
        this.telegramUserId = telegramUserId;
    }
}


package com.batrobot.bot.infrastructure.telegram.command.mapper;

import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.orchestration.application.dto.request.BindCommandRequest;
import com.batrobot.orchestration.application.dto.request.CommonRequest;
import com.batrobot.orchestration.application.dto.request.UnbindCommandRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * Mapper for commands that require only chat and user context.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommandRequestMapper extends SteamIdArgumentMapperSupport {

    CommonRequest toCommonRequest(CommandEnvelope.Payload source);

    @Mapping(target = "primaryBinding", ignore = true)
    @Mapping(target = "steamId64", source = "command.arguments", qualifiedByName = "firstArgToLong")
    BindCommandRequest toBindCommandRequest(CommandEnvelope.Payload source);

    @Mapping(target = "steamId64", source = "command.arguments", qualifiedByName = "firstArgToLong")
    UnbindCommandRequest toUnbindCommandRequest(CommandEnvelope.Payload source);

    @Named("firstArgToLong")
    default Long firstArgToLong(String[] args) {
        return parseSteamId(args, "common.exception.no_steam_id", "common.exception.invalid_steam_id");
    }
}

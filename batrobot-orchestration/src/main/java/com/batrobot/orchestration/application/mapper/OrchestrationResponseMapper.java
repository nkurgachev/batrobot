package com.batrobot.orchestration.application.mapper;

import com.batrobot.steam.application.dto.response.SteamPlayerResponse;
import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.rankhistory.application.dto.response.PlayerRankHistoryResponse;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.orchestration.application.dto.request.info.UserInfo;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse;
import com.batrobot.orchestration.application.dto.response.BindCommandResponse;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse;
import com.batrobot.orchestration.application.dto.response.MatchResultNotificationDataResponse.MatchNotificationTarget;
import com.batrobot.orchestration.application.dto.response.RepsCommandResponse;
import com.batrobot.orchestration.application.dto.response.SetEmojiCommandResponse;
import com.batrobot.orchestration.application.dto.response.UnbindCommandResponse;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory.PlayerMatchHistory;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory.PlayerMatchHistory.MatchStats;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse.UserGameStatus;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse.UserGameStatus.GameInfo;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse.PlayerRankHistory;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse.PlayerRankHistory.RankInfo;
import com.batrobot.orchestration.application.dto.response.RepsCommandResponse.UserReputation;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse.PuberInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for converting full domain response DTOs into optimized BFF
 * response.
 * 
 * Transforms complete Response objects from multiple domains into a single,
 * minimal DTO tailored for Telegram bot client consumption.
 * Implements BFF pattern: only necessary data is included, no payload bloat.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrchestrationResponseMapper {

    /**
     * Converts full domain responses to optimized BFF response.
     * Extracts only essential fields needed for Telegram bot message.
     * 
     * @param binding Chat-Player binding response
     * @param chat    Chat information
     * @param user    User information
     * @param player  Player information
     * @return Optimized BFF response with minimal payload
     */
    @Mapping(target = "bindingId", source = "binding.id")
    @Mapping(target = "telegramChatId", source = "chat.chatId")
    @Mapping(target = "telegramChatTitle", source = "chat.title")
    @Mapping(target = "telegramUserId", source = "user.telegramUserId")
    @Mapping(target = "telegramUsername", source = "user.username")
    @Mapping(target = "steamId64", source = "player.steamId64")
    @Mapping(target = "steamUsername", source = "player.steamUsername")
    @Mapping(target = "createdAt", source = "binding.createdAt")
    BindCommandResponse toOrchestrationResponse(
            ChatPlayerBindingResponse binding,
            ChatResponse chat,
            UserResponse user,
            PlayerResponse player);

    /**
     * Converts full domain responses to optimized BFF response for unbinding.
     * Extracts only essential fields needed for Telegram bot message.
     * 
     * @param binding
     * @param user
     * @param player
     * @return
     */
    @Mapping(target = "chatId", source = "binding.chatId")
    @Mapping(target = "telegramUserId", source = "binding.telegramUserId")
    @Mapping(target = "telegramUsername", source = "user.username")
    @Mapping(target = "steamId64", source = "binding.steamId64")
    @Mapping(target = "steamUsername", source = "player.steamUsername")
    UnbindCommandResponse toUnbindResponse(
            ChatPlayerBindingResponse binding,
            UserInfo user,
            PlayerResponse player);

    /**
     * Converts rank history response item to RankInfo DTO.
     *
     * @param history Rank history response item
     * @return RankInfo DTO
     */
    RankInfo toRankInfo(PlayerRankHistoryResponse.Rank history);

    /**
     * Converts player response and rank history list into orchestration response.
     *
     * @param player      Player response DTO
     * @param rankHistory Rank history items
     * @return PlayerRankHistory DTO
     */
    @Mapping(target = "steamId64", source = "player.steamId64")
    @Mapping(target = "steamUsername", source = "player.steamUsername")
    @Mapping(target = "rankHistory", source = "rankHistory")
    PlayerRankHistory toPlayerRankHistory(
            PlayerResponse player,
            List<RankInfo> rankHistory);

    /**
     * Converts domain player and stats into orchestration DTO for today's matches.
     */
    @Mapping(target = "steamId64", source = "player.steamId64")
    @Mapping(target = "steamUsername", source = "player.steamUsername")
    @Mapping(target = "matches", source = "matches")
    PlayerMatchHistory toPlayerTodayMatchesResponse(
            PlayerResponse player,
            List<MatchStats> matches);

    /**
     * Converts domain match and stats into orchestration match-with-stats DTO.
     */
    @Mapping(target = "matchId", expression = "java(match != null ? match.matchId() : stats.matchId())")
    @Mapping(target = "startDateTime", expression = "java(match != null ? match.startDateTime() : null)")
    @Mapping(target = "lobbyType", expression = "java(match != null ? match.lobbyType() : null)")
    @Mapping(target = "gameMode", expression = "java(match != null ? match.gameMode() : null)")
    @Mapping(target = "isVictory", source = "stats.isVictory")
    @Mapping(target = "heroName", source = "stats.heroName")
    @Mapping(target = "position", source = "stats.position")
    @Mapping(target = "kills", source = "stats.kills")
    @Mapping(target = "deaths", source = "stats.deaths")
    @Mapping(target = "assists", source = "stats.assists")
    @Mapping(target = "award", source = "stats.award")
    @Mapping(target = "imp", source = "stats.imp")
    MatchStats toMatchWithPlayerStats(
            MatchResponse match,
            PlayerMatchStatsResponse stats);

    /**
     * Converts user response and game info list into orchestration response.
     *
     * @param user     User response DTO
     * @param gameInfo List of games user is currently playing
     * @return UserGameStatus DTO
     */
    @Mapping(target = "telegramUserId", source = "user.telegramUserId")
    @Mapping(target = "telegramUsername", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "emoji", source = "user.emoji")
    @Mapping(target = "games", source = "gameInfo")
    UserGameStatus toUserGameStatus(
            UserResponse user,
            List<GameInfo> gameInfo);

    /**
     * Converts user response with reputation score into orchestration response.
     *
     * @param user       User response DTO
     * @param reputation User reputation score
     * @return UserReputation DTO
     */
    @Mapping(target = "telegramUsername", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "emoji", source = "user.emoji")
    @Mapping(target = "reputation", source = "reputation")
    UserReputation toUserReputation(
            UserResponse user,
            Integer reputation);

    /**
     * Converts Steam player response to GameInfo DTO.
     *
     * @param steamPlayer Steam player response
     * @return GameInfo DTO
     */
    GameInfo toGameInfo(SteamPlayerResponse steamPlayer);

    /**
     * Converts binding + user + player into TopPubers response item.
     *
     * @param binding Binding with Telegram and Steam IDs
     * @param user    Telegram user info
     * @param player  Steam player info
     * @return PuberInfo DTO
     */
    @Mapping(target = "telegramUserId", source = "binding.telegramUserId")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "telegramUsername", source = "user.username")
    @Mapping(target = "emoji", source = "user.emoji")
    @Mapping(target = "steamUsername", source = "player.steamUsername")
    @Mapping(target = "seasonRank", source = "player.seasonRank")
    PuberInfo toPuberInfo(
            ChatPlayerBindingResponse binding,
            UserResponse user,
            PlayerResponse player);

    /**
     * Converts binding + user + match metadata into match notification target.
     */
    @Mapping(target = "telegramChatId", source = "binding.chatId")
    @Mapping(target = "telegramUserId", source = "binding.telegramUserId")
    @Mapping(target = "telegramUsername", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "emoji", source = "user.emoji")
    MatchNotificationTarget toMatchNotificationTarget(
            ChatPlayerBindingResponse binding,
            UserResponse user,
            String steamUsername,
            String lobbyType,
            String gameMode);

    @Mapping(target = "telegramUserId", source = "telegramUserId")
    @Mapping(target = "telegramUsername", source = "username")
    @Mapping(target = "emoji", source = "emoji")
    SetEmojiCommandResponse toSetEmojiCommandResponse(UserResponse user);

    /**
     * Wraps rank history list into response.
     *
     * @param players list of player rank histories
     * @return MeCommandResponse DTO
     */
    default MeCommandResponse toMeCommandResponse(List<PlayerRankHistory> players) {
        return new MeCommandResponse(players);
    }

    /**
     * Wraps game status list into response.
     *
     * @param users list of user game statuses
     * @return InGameCommandResponse DTO
     */
    default InGameCommandResponse toInGameCommandResponse(List<UserGameStatus> users) {
        return new InGameCommandResponse(users);
    }

    /**
     * Wraps reputation list into response.
     *
     * @param users list of user reputations
     * @return RepsCommandResponse DTO
     */
    default RepsCommandResponse toRepsCommandResponse(List<UserReputation> users) {
        return new RepsCommandResponse(users);
    }

    /**
     * Wraps match history list into response.
     *
     * @param userMatches list of user match histories
     * @return AllPubsTodayCommandResponse DTO
     */
    default AllPubsTodayCommandResponse toAllPubsTodayCommandResponse(List<UserMatchHistory> userMatches) {
        return new AllPubsTodayCommandResponse(userMatches);
    }

    /**
     * Creates user match history entry.
     *
     * @param telegramUserId Telegram user ID
     * @param players        list of player match histories
     * @return UserMatchHistory DTO
     */
    default UserMatchHistory toUserMatchHistory(UserResponse user, List<PlayerMatchHistory> players) {
        return new UserMatchHistory(
                user.telegramUserId(),
                user.username(),
                user.firstName(),
                user.lastName(),
                user.emoji(),
                players);
    }

    /**
     * Creates player rank history when player details are missing.
     *
     * @param steamId64     Steam ID
     * @param steamUsername Steam username placeholder
     * @param rankHistory   rank history items
     * @return PlayerRankHistory DTO
     */
    default PlayerRankHistory toPlayerRankHistoryFallback(
            Long steamId64,
            String steamUsername,
            List<RankInfo> rankHistory) {
        return new PlayerRankHistory(steamId64, steamUsername, rankHistory);
    }

    /**
     * Creates user game status when user details are missing.
     *
     * @param telegramUserId Telegram user ID
     * @param games          list of games
     * @return UserGameStatus DTO
     */
    default UserGameStatus toUserGameStatusFallback(Long telegramUserId, List<GameInfo> games) {
        return new UserGameStatus(telegramUserId, "unknown", null, null, "👤", games);
    }

}

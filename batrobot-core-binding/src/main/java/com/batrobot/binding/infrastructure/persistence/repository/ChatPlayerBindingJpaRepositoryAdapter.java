package com.batrobot.binding.infrastructure.persistence.repository;

import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.binding.infrastructure.persistence.entity.ChatPlayerBindingEntity;
import com.batrobot.binding.infrastructure.persistence.mapper.ChatPlayerBindingEntityMapper;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.shared.infrastructure.persistence.repository.RepositoryAdapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter: Implements domain ChatPlayerBindingRepository using JPA layer.
 */
@Repository
@RequiredArgsConstructor
public class ChatPlayerBindingJpaRepositoryAdapter
        extends RepositoryAdapter<ChatPlayerBinding, ChatPlayerBindingEntity, UUID>
        implements ChatPlayerBindingRepository {

    private final ChatPlayerBindingJpaRepository jpaRepository;
    private final ChatPlayerBindingEntityMapper mapper;

    @Override
    public Optional<ChatPlayerBinding> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ChatPlayerBinding> findBindingForUser(
            TelegramChatId chatId,
            TelegramUserId userId,
            SteamId steamId) {
        return jpaRepository.findBinding(chatId.value(), userId.value(), steamId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ChatPlayerBinding> findBindingInChatBySteamId(
            TelegramChatId chatId,
            SteamId steamId) {
        return jpaRepository.findBindingInChatBySteamId(chatId.value(), steamId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<ChatPlayerBinding> findBindingsForUserInChat(
            TelegramChatId chatId,
            TelegramUserId userId) {
        return jpaRepository.findUserBindingsInChat(chatId.value(), userId.value())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatPlayerBinding> findBindingsInChat(TelegramChatId chatId) {
        return jpaRepository.findBindingsInChat(chatId.value())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatPlayerBinding> findBindingsForSteamAccount(SteamId steamId) {
        return jpaRepository.findBindingsForSteamAccount(steamId.value())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChatPlayerBinding> findPrimaryBindingForUserInChat(
            TelegramChatId chatId,
            TelegramUserId userId) {
        return jpaRepository.findPrimaryBinding(chatId.value(), userId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Long> findAllBoundSteamIds() {
        return jpaRepository.findAllBoundSteamIds();
    }

    @Override
    public ChatPlayerBinding save(ChatPlayerBinding binding) {
        ChatPlayerBindingEntity entity = mapper.toEntity(binding);
        ChatPlayerBindingEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        validateExists(id);
        jpaRepository.deleteById(id);
    }

    @Override
    protected ChatPlayerBinding toDomain(ChatPlayerBindingEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected ChatPlayerBindingEntity toEntity(ChatPlayerBinding aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected ChatPlayerBindingEntity persistEntity(ChatPlayerBindingEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<ChatPlayerBinding> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

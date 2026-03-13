package com.batrobot.chat.infrastructure.persistence.repository;

import com.batrobot.chat.domain.model.Chat;
import com.batrobot.chat.domain.repository.ChatRepository;
import com.batrobot.chat.infrastructure.persistence.entity.ChatEntity;
import com.batrobot.chat.infrastructure.persistence.mapper.ChatEntityMapper;
import com.batrobot.shared.domain.model.valueobject.*;
import com.batrobot.shared.infrastructure.persistence.repository.RepositoryAdapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementation of TelegramChatRepository (domain interface).
 */
@Repository
@RequiredArgsConstructor
public class ChatJpaRepositoryAdapter extends RepositoryAdapter<Chat, ChatEntity, UUID>
        implements ChatRepository {
    
    private final ChatJpaRepository jpaRepository; // JPA repository
    private final ChatEntityMapper mapper;
    
    @Override
    public Optional<Chat> findById(UUID chatId) {
        return jpaRepository.findById(chatId)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Chat> findAllById(Collection<UUID> chatIds) {
        return jpaRepository.findAllById(chatIds)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Chat save(Chat chat) {
        return saveAggregate(chat);
    }
    
    @Override
    public boolean existsById(UUID chatId) {
        return jpaRepository.existsById(chatId);
    }
    
    @Override
    public void deleteById(UUID chatId) {
        validateExists(chatId);
        jpaRepository.deleteById(chatId);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<Chat> findByTelegramChatId(TelegramChatId telegramChatId) {
        return jpaRepository.findByTelegramChatId(telegramChatId.value())
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByTelegramChatId(TelegramChatId telegramChatId) {
        return jpaRepository.existsByTelegramChatId(telegramChatId.value());
    }

    @Override
    public List<Chat> findAllByTelegramChatId(Collection<TelegramChatId> telegramChatIds) {
        List<Long> primitiveIds = telegramChatIds.stream()
            .map(TelegramChatId::value)
            .collect(Collectors.toList());
        
        return jpaRepository.findAllByTelegramChatIdIn(primitiveIds)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    protected Chat toDomain(ChatEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected ChatEntity toEntity(Chat aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected ChatEntity persistEntity(ChatEntity entity) {
        return jpaRepository.save(entity);
    }
}


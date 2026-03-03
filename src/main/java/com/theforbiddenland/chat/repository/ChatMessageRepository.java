package com.theforbiddenland.chat.repository;

import com.theforbiddenland.chat.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {
}

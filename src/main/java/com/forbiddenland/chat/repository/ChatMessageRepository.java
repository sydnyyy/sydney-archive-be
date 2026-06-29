package com.forbiddenland.chat.repository;

import com.forbiddenland.chat.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {
}

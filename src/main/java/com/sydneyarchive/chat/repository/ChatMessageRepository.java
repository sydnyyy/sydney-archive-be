package com.sydneyarchive.chat.repository;

import com.sydneyarchive.chat.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {
}

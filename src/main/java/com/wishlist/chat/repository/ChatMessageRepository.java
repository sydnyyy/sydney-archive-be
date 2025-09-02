package com.wishlist.chat.repository;

import com.wishlist.chat.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {
}

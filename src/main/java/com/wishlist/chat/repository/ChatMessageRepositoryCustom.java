package com.wishlist.chat.repository;

import com.wishlist.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessageEntity> findByUserIdAndBeforeId(String userId, String lastId, int limit);
}

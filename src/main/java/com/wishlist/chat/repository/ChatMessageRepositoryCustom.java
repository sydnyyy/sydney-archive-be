package com.wishlist.chat.repository;

import com.wishlist.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessageEntity> findByClientIdBeforeId(String clientId, String lastId, int limit);
}

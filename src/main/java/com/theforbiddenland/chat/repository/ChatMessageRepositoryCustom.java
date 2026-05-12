package com.theforbiddenland.chat.repository;

import com.theforbiddenland.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessageEntity> findBySidAndBeforeId(String sid, String lastId, int limit);
}

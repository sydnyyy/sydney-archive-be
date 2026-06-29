package com.forbiddenland.chat.repository;

import com.forbiddenland.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessageEntity> findBySidAndBeforeId(String sid, String lastId, int limit);
}

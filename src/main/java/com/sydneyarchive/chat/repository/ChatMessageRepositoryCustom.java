package com.sydneyarchive.chat.repository;

import com.sydneyarchive.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessageEntity> findBySidAndBeforeId(String sid, String lastId, int limit);
}

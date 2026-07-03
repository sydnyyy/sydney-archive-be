package com.sydneyarchive.chat.repository;

import com.sydneyarchive.chat.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessage> findByChatRoomIdAndBeforeCursor(String chatRoomId, String lastId, int limit);
}

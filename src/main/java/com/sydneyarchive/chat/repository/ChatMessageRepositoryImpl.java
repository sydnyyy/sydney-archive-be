package com.sydneyarchive.chat.repository;

import com.sydneyarchive.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatMessage> findByChatRoomIdAndBeforeCursor(
            String chatRoomId,
            String cursorId,
            int limit
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomId").is(chatRoomId));

        if (cursorId != null && !cursorId.isBlank()) {
            ChatMessage cursor = mongoTemplate.findById(cursorId, ChatMessage.class);

            if (cursor != null) {
                query.addCriteria(
                        Criteria.where("createdAt").lt(cursor.getCreatedAt())
                );
            }
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(limit);

        List<ChatMessage> results = mongoTemplate.find(query, ChatMessage.class);
        Collections.reverse(results);
        return results;
    }
}
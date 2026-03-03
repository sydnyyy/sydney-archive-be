package com.wishlist.chat.repository;

import com.wishlist.chat.entity.ChatMessageEntity;
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
    public List<ChatMessageEntity> findByUserIdAndBeforeId(String userId, String lastId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        if (lastId != null && !lastId.isEmpty()) {
            query.addCriteria(Criteria.where("_id").lt(lastId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(limit);

        List<ChatMessageEntity> results = mongoTemplate.find(query, ChatMessageEntity.class);
        Collections.reverse(results);
        return results;
    }
}
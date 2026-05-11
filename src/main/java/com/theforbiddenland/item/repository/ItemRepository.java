package com.theforbiddenland.item.repository;

import com.theforbiddenland.item.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}

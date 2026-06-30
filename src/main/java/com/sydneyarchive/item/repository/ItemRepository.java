package com.sydneyarchive.item.repository;

import com.sydneyarchive.item.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}

package com.sydneyarchive.item.repository;

import com.sydneyarchive.common.enums.VisibilityStatus;
import com.sydneyarchive.item.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {

    List<Item> findByVisibilityStatus(VisibilityStatus visibilityStatus);
}

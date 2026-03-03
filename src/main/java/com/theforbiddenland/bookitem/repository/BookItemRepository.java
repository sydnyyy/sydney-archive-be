package com.theforbiddenland.bookitem.repository;

import com.theforbiddenland.bookitem.entity.BookItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookItemRepository extends MongoRepository<BookItem, String> {
}

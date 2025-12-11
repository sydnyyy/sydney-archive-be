package com.wishlist.bookitem.repository;

import com.wishlist.bookitem.entity.BookItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookItemRepository extends MongoRepository<BookItem, String> {
}

package com.wishlist.bookitem.dto;

import com.wishlist.global.item.ItemType;

import java.util.List;

public record BookItemCreateRequest(

        ItemType itemType,
        String title,
        String author,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex
) { }

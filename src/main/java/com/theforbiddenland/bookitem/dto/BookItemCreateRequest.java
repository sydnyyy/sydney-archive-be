package com.theforbiddenland.bookitem.dto;

import com.theforbiddenland.global.item.ItemType;

import java.util.List;

public record BookItemCreateRequest(

        ItemType itemType,
        String title,
        String author,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex
) { }

package com.wishlist.useractivity.service;

import com.wishlist.useractivity.entity.AccessEvent;
import com.wishlist.useractivity.repository.AccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final AccessRepository accessRepository;

    public void recordAccess(AccessEvent accessEvent) {
        accessRepository.save(accessEvent);
    }
}

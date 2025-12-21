package com.wishlist.user.service;

import com.mongodb.DuplicateKeyException;
import com.wishlist.auth.dto.OAuth2Profile;
import com.wishlist.user.dto.UserResponse;
import com.wishlist.user.dto.UserSummaryResponse;
import com.wishlist.user.entity.User;
import com.wishlist.user.enums.Role;
import com.wishlist.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void saveGuest(String uid) {
        boolean isExist = userRepository.existsByUid(uid);
        if (isExist) return;

        try {
            User guest = User.of(Role.GUEST, uid);
            userRepository.save(guest);
        } catch (DuplicateKeyException e) {
            log.warn("[saveGuest] GUEST 중복 삽입 시도 uid={}", uid);
        }
    }

    public void saveOrUpdate(OAuth2Profile oauth2Profile) {
        User user = userRepository.findByProviderAndProviderId(
                oauth2Profile.provider(), oauth2Profile.providerId())
//                .map(entity -> entity.update(OAuth2Profile))
                .orElseGet(() -> User.of(oauth2Profile));

        userRepository.save(user);
    }

    public void updateLastMessageAt(String uid, Instant sendAt) {
        userRepository.findByUid(uid).ifPresentOrElse(
                user -> {
                    user.updateLastMessageAt(sendAt);
                    userRepository.save(user);
                },
                () -> {
                    User guest = User.of(Role.GUEST, uid);
                    guest.updateLastMessageAt(sendAt);
                    userRepository.save(guest);
                }
        );
    }

    public List<UserResponse> findRecentChatUsers() {
        return userRepository.findAllByOrderByLastMessageAtDesc()
                .stream()
                .map(UserResponse::of)
                .toList();
    }

    public UserSummaryResponse findUserSummaryByUid(String uid) {
        return UserSummaryResponse.ofOrUnknown(userRepository.findByUid(uid));
    }

    public UserSummaryResponse findUserSummaryByUserId(String userId) {
        return UserSummaryResponse.ofOrUnknown(userRepository.findById(userId));
    }
}

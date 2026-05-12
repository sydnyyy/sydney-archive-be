package com.theforbiddenland.user.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.mongodb.DuplicateKeyException;
import com.theforbiddenland.auth.dto.OAuth2Profile;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.UserException;
import com.theforbiddenland.user.dto.UserResponse;
import com.theforbiddenland.user.dto.UserSummaryResponse;
import com.theforbiddenland.user.entity.User;
import com.theforbiddenland.user.enums.Role;
import com.theforbiddenland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void saveGuest(String sid) {
        boolean isExist = userRepository.existsBySid(sid);
        if (isExist) return;

        try {
            User guest = User.of(Role.GUEST, sid);
            userRepository.save(guest);
        } catch (DuplicateKeyException e) {
            log.warn("[saveGuest] GUEST 중복 삽입 시도 sid={}", sid);
        }
    }

    public String saveGuest() {
        String sid = NanoIdUtils.randomNanoId();

        try {
            User guest = User.of(Role.GUEST, sid);
            userRepository.save(guest);
            return sid;
        } catch (DuplicateKeyException e) {
            log.warn("[saveGuest] GUEST 중복 삽입 시도 sid={}", sid);
            throw new UserException(ErrorCode.SID_CREATION_FAILED);
        }
    }

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void saveOrUpdate(OAuth2Profile oauth2Profile) {
        User user = userRepository.findByProviderAndProviderId(
                oauth2Profile.provider(), oauth2Profile.providerId())
//                .map(entity -> entity.update(OAuth2Profile))
                .orElseGet(() -> {
                    String sid = NanoIdUtils.randomNanoId();
                    return User.of(oauth2Profile, sid);
                });

        userRepository.save(user);
    }

    @Recover
    public void recover(DuplicateKeyException e, OAuth2Profile oAuth2Profile) {
        log.error("[UserService] SID 생성 실패 (중복 지속 발생). oauth2Profile.provider={}", oAuth2Profile.provider());
        throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR, "SID 생성 중복 오류");
    }

    public void updateLastMessageAt(String sid, Instant sendAt) {
        userRepository.findBySid(sid).ifPresentOrElse(
                user -> {
                    user.updateLastMessageAt(sendAt);
                    userRepository.save(user);
                },
                () -> {
                    User guest = User.of(Role.GUEST, sid);
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

    public UserSummaryResponse findUserSummaryBySid(String sid) {
        return UserSummaryResponse.ofOrUnknown(userRepository.findBySid(sid));
    }

    public void addLike(String sid, String itemId) {
        User user = userRepository.findBySid(sid)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        user.addLikedItemId(itemId);
        userRepository.save(user);
    }

    public void deleteLike(String sid, String itemId) {
        User user = userRepository.findBySid(sid)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        user.deleteLikeItemId(itemId);
        userRepository.save(user);
    }

    public List<String> getLikedItemIds(String sid) {
        User user = userRepository.findBySid(sid)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<String> likedItemIds = new ArrayList<>(user.getLikedItemIds());
        Collections.reverse(likedItemIds);
        return likedItemIds;
    }
}

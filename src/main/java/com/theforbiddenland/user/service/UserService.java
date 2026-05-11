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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void saveGuest(String uid) {
        boolean isExist = userRepository.existsBySid(uid);
        if (isExist) return;

        try {
            User guest = User.of(Role.GUEST, uid);
            userRepository.save(guest);
        } catch (DuplicateKeyException e) {
            log.warn("[saveGuest] GUEST 중복 삽입 시도 uid={}", uid);
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
                    String uid = NanoIdUtils.randomNanoId();
                    return User.of(oauth2Profile, uid);
                });

        userRepository.save(user);
    }

    @Recover
    public void recover(DuplicateKeyException e, OAuth2Profile oAuth2Profile) {
        log.error("[UserService] UID 생성 실패 (중복 지속 발생). oauth2Profile.provider={}", oAuth2Profile.provider());
        throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR, "UID 생성 중복 오류");
    }

    public String findUserIdByUid(String uid) {
        Optional<User> userOptional = userRepository.findBySid(uid);
        if (userOptional.isEmpty()) {
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }

        return userOptional.get().getId();
    }

    public String findUidByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }

        return userOptional.get().getSid();
    }

    public void updateLastMessageAt(String uid, Instant sendAt) {
        userRepository.findBySid(uid).ifPresentOrElse(
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

    public UserSummaryResponse findUserSummaryByUid(String sid) {
        return UserSummaryResponse.ofOrUnknown(userRepository.findBySid(sid));
    }
}

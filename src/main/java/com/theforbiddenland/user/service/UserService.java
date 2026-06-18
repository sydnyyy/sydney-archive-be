package com.theforbiddenland.user.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.mongodb.DuplicateKeyException;
import com.theforbiddenland.auth.dto.internal.CustomOAuth2User;
import com.theforbiddenland.global.config.auth.AdminProperties;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.UserException;
import com.theforbiddenland.user.dto.UserResponse;
import com.theforbiddenland.user.dto.internal.UserAuthContext;
import com.theforbiddenland.user.dto.response.AdminResponse;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AdminProperties adminProperties;

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
    public UserAuthContext saveAdmin(CustomOAuth2User customOAuth2User) {
        User user = userRepository.findByProviderAndProviderId(customOAuth2User.getProvider(), customOAuth2User.getProviderId())
                .orElseGet(() -> {
                    String sid = NanoIdUtils.randomNanoId();
                    return userRepository.save(User.of(customOAuth2User, sid, adminProperties.username()));
                });

        return UserAuthContext.of(user);
    }

    @Recover
    public UserAuthContext recover(DuplicateKeyException e, CustomOAuth2User customOAuth2User) {
        log.error("[UserService] SID 생성 실패 (중복 지속 발생). oauth2Profile.provider={}", customOAuth2User.getProvider());
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

    public AdminResponse getAdmin(String userId) {
        return userRepository.findById(userId)
                .map(AdminResponse::of)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public UserAuthContext getUserContext(String userId) {
        return userRepository.findById(userId)
                .map(UserAuthContext::of)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }
}

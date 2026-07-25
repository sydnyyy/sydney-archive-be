package com.sydneyarchive.user.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.mongodb.DuplicateKeyException;
import com.sydneyarchive.auth.dto.internal.CustomOAuth2User;
import com.sydneyarchive.global.config.auth.AdminProperties;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.UserException;
import com.sydneyarchive.user.dto.internal.UserAuthContext;
import com.sydneyarchive.user.dto.internal.UserContext;
import com.sydneyarchive.user.dto.response.AdminResponse;
import com.sydneyarchive.user.entity.User;
import com.sydneyarchive.user.repository.UserRepository;
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
    private final AdminProperties adminProperties;

    public String saveGuest() {
        String sid = NanoIdUtils.randomNanoId();

        try {
            User guest = User.createGuest(sid);
            userRepository.save(guest);
            return sid;
        } catch (DuplicateKeyException e) {
            log.warn("[saveGuest] GUEST 중복 삽입 시도 sid={}", sid);
            throw new UserException(ErrorCode.SID_CREATION_FAILED);
        }
    }

    @Retryable(
            retryFor = org.springframework.dao.DuplicateKeyException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public UserAuthContext saveAdmin(CustomOAuth2User customOAuth2User) {
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(customOAuth2User.getProvider(), customOAuth2User.getProviderId());

        if (userOptional.isPresent()) {
            return UserAuthContext.of(userOptional.get(), false);
        }

        String sid = NanoIdUtils.randomNanoId();
        User user = User.createAdmin(customOAuth2User, sid, adminProperties.username());
        userRepository.save(user);

        return UserAuthContext.of(user, true);
    }

    @Recover
    public UserAuthContext recover(DuplicateKeyException e, CustomOAuth2User customOAuth2User) {
        log.error("[UserService] SID 생성 실패 (중복 지속 발생). oauth2Profile.provider={}", customOAuth2User.getProvider());
        throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR, "SID 생성 중복 오류");
    }

    public void updateLastMessageAt(String sid, Instant sendAt) {
        userRepository.findBySid(sid).ifPresent(
                user -> {
                    user.updateLastMessageAt(sendAt);
                    userRepository.save(user);
                }
        );
    }

    public List<UserContext> findAllUsersHavingLastMessage() {
        return userRepository.findAllByLastMessageAtIsNotNullOrderByLastMessageAtDesc()
                .stream()
                .map(UserContext::of)
                .toList();
    }

    public AdminResponse getAdmin(String userId) {
        return userRepository.findById(userId)
                .map(AdminResponse::of)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    // only admin
    public UserAuthContext getUserContextById(String userId) {
        return userRepository.findById(userId)
                .map(u -> UserAuthContext.of(u, false))
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public UserAuthContext getUserContextBySid(String sid) {
        return userRepository.findBySid(sid)
                .map(u -> UserAuthContext.of(u, false))
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public void deleteAdmin(String userId) {
        userRepository.deleteById(userId);
    }
}

package com.sydneyarchive.user.service;

import com.sydneyarchive.auth.dto.internal.CustomOAuth2User;
import com.sydneyarchive.global.config.auth.AdminProperties;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.UserException;
import com.sydneyarchive.user.dto.internal.UserAuthContext;
import com.sydneyarchive.user.dto.internal.UserContext;
import com.sydneyarchive.user.dto.response.UserResponse;
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

    @Retryable(
            retryFor = { org.springframework.dao.DuplicateKeyException.class, Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public UserAuthContext saveGuest() {
        User user = userRepository.save(User.createGuest());
        return UserAuthContext.of(user, true);
    }

    @Recover
    public String recover(Throwable t) {
        log.error("[UserService] Duplicate key collision during GUEST generation. message={}", t.getMessage());
        throw new UserException(ErrorCode.DUPLICATE_USER_SID);
    }

    @Retryable(
            retryFor = { org.springframework.dao.DuplicateKeyException.class, Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public UserAuthContext saveAdmin(CustomOAuth2User customOAuth2User) {
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(customOAuth2User.getProvider(), customOAuth2User.getProviderId());

        if (userOptional.isPresent()) {
            return UserAuthContext.of(userOptional.get(), false);
        }

        User user = User.createAdmin(customOAuth2User, adminProperties.username());
        userRepository.save(user);

        return UserAuthContext.of(user, true);
    }

    @Recover
    public UserAuthContext recover(Throwable t, CustomOAuth2User customOAuth2User) {
        log.error("[UserService] Duplicate key collision during ADMIN generation. message={}", t.getMessage());
        throw new UserException(ErrorCode.DUPLICATE_USER_SID);
    }

    public void updateLastMessageAt(String userId, Instant sendAt) {
        userRepository.findById(userId).ifPresent(
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

    public UserResponse getUserInfo(String userId) {
        return userRepository.findById(userId)
                .map(UserResponse::of)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public UserAuthContext getUserContext(String userId) {
        return userRepository.findById(userId)
                .map(u -> UserAuthContext.of(u, false))
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public void deleteAdmin(String userId) {
        userRepository.deleteById(userId);
    }
}

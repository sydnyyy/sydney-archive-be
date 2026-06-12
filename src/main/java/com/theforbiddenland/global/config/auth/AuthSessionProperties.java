package com.theforbiddenland.global.config.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.auth-session")
@Setter
@Getter
public class AuthSessionProperties {

    private int authSessionTimeoutSec;
}

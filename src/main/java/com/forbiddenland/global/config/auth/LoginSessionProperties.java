package com.forbiddenland.global.config.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.login-session")
@Setter
@Getter
public class LoginSessionProperties {

    private int loginSessionTimeoutSec;
}

package com.theforbiddenland.global.config.system;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.system")
@Setter
@Getter
public class SystemProperties {

    private int bufferTimeSec;
}

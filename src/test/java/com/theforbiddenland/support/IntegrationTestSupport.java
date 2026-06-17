package com.theforbiddenland.support;

import com.theforbiddenland.config.IntegrationTestInitializer;
import com.theforbiddenland.global.config.dotenv.DotenvInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = {DotenvInitializer.class, IntegrationTestInitializer.class})
public abstract class IntegrationTestSupport {
}

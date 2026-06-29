package com.forbiddenland.support;

import com.forbiddenland.config.IntegrationTestInitializer;
import com.forbiddenland.global.config.dotenv.DotenvInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = {DotenvInitializer.class, IntegrationTestInitializer.class})
public abstract class IntegrationTestSupport {
}

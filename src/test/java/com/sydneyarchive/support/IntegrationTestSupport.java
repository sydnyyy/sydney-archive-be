package com.sydneyarchive.support;

import com.sydneyarchive.config.IntegrationTestInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = {IntegrationTestInitializer.class})
public abstract class IntegrationTestSupport {
}

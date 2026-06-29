package com.forbiddenland.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

public class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

    public static final MongoDBContainer MONGO_CONTAINER =
            new MongoDBContainer(DockerImageName.parse("mongo:8.0"));

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Stream.of(REDIS_CONTAINER, MONGO_CONTAINER)
                .parallel()
                .forEach(container -> {
                    if (!container.isRunning()) {
                        container.start();
                    }
                });

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.data.redis.host=" + REDIS_CONTAINER.getHost(),
                "spring.data.redis.port=" + REDIS_CONTAINER.getMappedPort(6379),
                "spring.data.mongodb.uri=" + MONGO_CONTAINER.getReplicaSetUrl()
        );
    }
}

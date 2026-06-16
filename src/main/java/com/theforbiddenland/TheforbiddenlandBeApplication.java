package com.theforbiddenland;

import com.theforbiddenland.global.config.dotenv.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class TheforbiddenlandBeApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TheforbiddenlandBeApplication.class);

        application.addInitializers(new DotenvInitializer());

        application.run(args);
    }

}

package com.mautz.ticketrush.ticketrush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class TicketrushApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketrushApplication.class, args);
    }

}

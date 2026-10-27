package com.shakeup.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, WgerProperties.class, OffProperties.class})
public class AppConfig {
}

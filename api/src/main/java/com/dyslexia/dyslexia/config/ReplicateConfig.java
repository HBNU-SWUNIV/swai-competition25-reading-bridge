package com.dyslexia.dyslexia.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "replicate.api")
@Getter
@Setter
public class ReplicateConfig {
    private String key;
    private String url;
    private String model;
}

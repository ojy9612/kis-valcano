package com.zeki.kisvolcano.domain._common.web_client.statics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keys")
@Getter
@Setter
public class ApiStatics {
    private Webhook webhook;

    @Getter
    @Setter
    public static class Webhook {
        private String url;
        private String key;
        private String token;
    }
}

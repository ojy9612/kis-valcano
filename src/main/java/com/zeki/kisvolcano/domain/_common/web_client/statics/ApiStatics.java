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
    private Kis kis;
    private DataGo dataGo;

    @Getter
    @Setter
    public static class Webhook {
        private String url;
        private String key;
        private String token;
    }

    @Getter
    @Setter
    public static class Kis {
        private String url;
        private String appKey;
        private String appSecret;
        private String accountNumber;
    }

    @Getter
    @Setter
    public static class DataGo {
        private String encoding;
        private String decoding;
    }
}

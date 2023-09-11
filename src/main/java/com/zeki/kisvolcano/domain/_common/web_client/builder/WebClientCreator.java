package com.zeki.kisvolcano.domain._common.web_client.builder;

import com.zeki.kisvolcano.domain._common.em.TradeMode;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class WebClientCreator {
    private final WebClient.Builder webClientBuilder;


    /**
     * WebClient의 baseUrl과 defaultHeader, encoding 설정
     *
     * @return {@link WebClient}
     */
    public WebClient setBaseUrl(String baseUrl) {
        // 인코딩 설정
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        // memory size 설정
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // to unlimited memory size
                .build();

        // timeout 설정
        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(
                HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120000)
                        .responseTimeout(Duration.ofSeconds(120)));


        return this.webClientBuilder
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(httpConnector)
                .uriBuilderFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(baseUrl)
                .build();
    }


    /**
     * WebClient TLS 1.3 적용 버전
     *
     * @return {@link WebClient}
     */
    public WebClient setBaseUrlKis(String baseUrl) {

        // 인코딩 설정
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        // memory size 설정
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // to unlimited memory size
                .build();


        // The connection observed an error  reactor.netty.http.client.PrematureCloseException: Connection prematurely closed BEFORE response
        // 위 에러 떄문에 추가 함.(패킷 단계에서 분석해야 함) 'io.micrometer:micrometer-core' 를 dependency 에 추가해야 함.
        ConnectionProvider provider = ConnectionProvider.builder("custom-provider")
                .maxConnections(100)
                .maxIdleTime(Duration.ofSeconds(3))
                .maxLifeTime(Duration.ofSeconds(3))
                .pendingAcquireTimeout(Duration.ofMillis(5000))
                .pendingAcquireMaxCount(-1)
                .evictInBackground(Duration.ofSeconds(30))
                .lifo()
                .metrics(true)
                .build();

        final SslContext sslContextForTls13;
        try {
            sslContextForTls13 = SslContextBuilder.forClient()
                    .protocols("TLSv1.3")
                    .build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }

        final HttpClient httpClientForTls13 = HttpClient.create(provider)
                .secure(ssl -> ssl.sslContext(sslContextForTls13))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120000) // timeout 설정
                .responseTimeout(Duration.ofSeconds(120));

        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(httpClientForTls13);

        return this.webClientBuilder
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(reactorClientHttpConnector)
                .uriBuilderFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(baseUrl)
                .build();
    }

}

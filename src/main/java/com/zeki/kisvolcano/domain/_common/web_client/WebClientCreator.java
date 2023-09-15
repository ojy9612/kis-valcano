package com.zeki.kisvolcano.domain._common.web_client;

import com.zeki.kisvolcano.domain._common.web_client.statics.ApiStatics;
import com.zeki.kisvolcano.exception.APIException;
import com.zeki.kisvolcano.exception.ResponseCode;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Log4j2
@RequiredArgsConstructor
@Configuration
public class WebClientCreator {
    private final WebClient.Builder webClientBuilder;
    private final ApiStatics apiStatics;


    /**
     * WebClient의 baseUrl과 defaultHeader, encoding 설정
     *
     * @return {@link WebClient}
     */
    @Bean
    @Qualifier("WebClient")
    public WebClient setBaseUrl() {
        String baseUrl = "";

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
    @Bean
    @Qualifier("WebClientKIS")
    public WebClient setBaseUrlKis() {
        String baseUrl = apiStatics.getKis().getUrl();

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
            throw new APIException(ResponseCode.INTERNAL_SERVER_WEBCLIENT_ERROR, e.getMessage());
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

    /**
     * WebClient TLS 1.3 적용 버전
     *
     * @return {@link WebClient}
     */
    @Bean
    @Qualifier("WebClientDataGo")
    public WebClient setBaseUrlDataGo() {
        String url = apiStatics.getDataGo().getUrl() + "?serviceKey=" + apiStatics.getDataGo().getEncoding();

        // 인코딩 설정
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        // memory size 설정
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // to unlimited memory size
                .build();


        final SslContext sslContext;
        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            throw new APIException(ResponseCode.INTERNAL_SERVER_WEBCLIENT_ERROR, "SSL 설정 오류");
        }

        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(
                HttpClient.create()
                        .secure(t -> t.sslContext(sslContext))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120000) // timeout 설정
                        .responseTimeout(Duration.ofSeconds(120)));

        return this.webClientBuilder
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(reactorClientHttpConnector)
                .uriBuilderFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(url)
                .build();
    }

}

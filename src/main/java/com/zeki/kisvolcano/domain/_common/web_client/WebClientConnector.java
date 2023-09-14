package com.zeki.kisvolcano.domain._common.web_client;


import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Log4j2
@Component
public class WebClientConnector {
    private final WebClient webClient;
    private final WebClient webClientKis;
    private final WebClient webClientDataGo;


    public WebClientConnector(@Qualifier("WebClient") WebClient webClient,
                              @Qualifier("WebClientKIS") WebClient webClientKis,
                              @Qualifier("WebClientDataGo") WebClient webClientDataGo) {
        this.webClient = webClient;
        this.webClientKis = webClientKis;
        this.webClientDataGo = webClientDataGo;
    }

    @Builder(builderMethodName = "connectBuilder", builderClassName = "ConnectBuilder")
    public <Q, S> ResponseEntity<S> connect(HttpMethod method, String path, Map<String, String> requestHeaders, MultiValueMap<String, String> requestParams, Q requestBody, Class<S> classType) {

        return webClient
                .method(method)
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(requestParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.setAll(requestHeaders == null ? Map.of() : requestHeaders))
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                .block();
    }

    @Builder(builderMethodName = "connectKisApiBuilder", builderClassName = "ConnectKisApiBuilder")
    public <Q, S> ResponseEntity<S> connectKisApi(HttpMethod method, String path, Map<String, String> requestHeaders, MultiValueMap<String, String> requestParams, Q requestBody, Class<S> classType) {

        return webClientKis
                .method(method)
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(requestParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.setAll(requestHeaders == null ? Map.of() : requestHeaders))
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                .block();
    }

    @Builder(builderMethodName = "connectDataGoApiBuilder", builderClassName = "ConnectDataGoApiBuilder")
    public <Q, S> ResponseEntity<S> connectDataGoApi(HttpMethod method, String path, Map<String, String> requestHeaders, MultiValueMap<String, String> requestParams, Q requestBody, Class<S> classType) {

        return webClientDataGo
                .method(method)
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(requestParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.setAll(requestHeaders == null ? Map.of() : requestHeaders))
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                .block();
    }

}

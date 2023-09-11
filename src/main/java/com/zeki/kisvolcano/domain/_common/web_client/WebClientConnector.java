package com.zeki.kisvolcano.domain._common.web_client;


import com.zeki.kisvolcano.domain._common.web_client.builder.WebClientCreator;
import com.zeki.kisvolcano.domain._common.web_client.statics.ApiStatics;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class WebClientConnector {
    private final WebClientCreator webClientCreator;
    private final ApiStatics statics;

    @Builder(builderMethodName = "connectKisApiBuilder", builderClassName = "ConnectKisApiBuilder")
    public <Q, S> ResponseEntity<S> connectKisApi(HttpMethod method, String path, Map<String,String> requestHeaders, MultiValueMap<String,String> requestParams, Q requestBody, Class<S> classType){

        return webClientCreator.setBaseUrlKis(statics.getKis().getUrl())
                .method(method)
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(requestParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.setAll(requestHeaders))
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                .block();
    }

}

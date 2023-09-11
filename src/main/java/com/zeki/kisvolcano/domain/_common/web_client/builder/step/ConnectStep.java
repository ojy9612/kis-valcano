package com.zeki.kisvolcano.domain._common.web_client.builder.step;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface ConnectStep {
    ResponseStep connectBlock(Map<String, String> headers, Class<?> responseType);
    ResponseStep connectBlock();
    Mono<?> connectSubscribe(Map<String, String> headers, Class<?> responseType);
    Mono<String> connectSubscribe();
}

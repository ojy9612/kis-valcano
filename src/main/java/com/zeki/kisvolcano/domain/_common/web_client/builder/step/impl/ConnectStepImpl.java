package com.zeki.kisvolcano.domain._common.web_client.builder.step.impl;


import com.zeki.kisvolcano.domain._common.web_client.builder.step.ConnectStep;
import com.zeki.kisvolcano.domain._common.web_client.builder.step.ResponseStep;
import com.zeki.kisvolcano.exception.APIException;
import com.zeki.kisvolcano.exception.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class ConnectStepImpl implements ConnectStep {

    private final WebClient.RequestHeadersSpec<?> methodType;
    private Object response;

    /**
     * WebClient의 header와 response class 설정 후 block으로 호출<br>
     * 헤더와 응답값이 존재할 때 사용. (ex. 일반적인 api 호출)<br>
     * 예외처리 포함
     * @return {@link ResponseStep}
     */
    @Override
    public ResponseStep connectBlock(Map<String, String> headers, Class<?> responseType) {
        try {
            this.response = this.methodType
                    .headers(httpHeaders -> httpHeaders.setAll(headers == null || headers.isEmpty() ? new HashMap<>() : headers))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new APIException(ResponseCode.INTERNAL_SERVER_ERROR, msg))))
                    .bodyToMono(responseType)
                    .retryWhen(Retry.fixedDelay(3, java.time.Duration.ofSeconds(1))
                            .doBeforeRetry(before -> log.info("Retry: {} | {}", before.totalRetries(), before.failure())))
                    .block();
            return new ResponseStepImpl(this.response);
        } catch (Exception e) {
            log.error("Exception: {} | {}", e.getMessage(), e.getStackTrace()[0].toString());
            throw new APIException(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * WebClient의 header와 response class 설정 후 block으로 호출<br>
     * 헤더와 응답값이 존재하지 않을 때 사용. (ex. Google chat webhook api 호출)<br>
     * 예외처리 포함
     * @return {@link ResponseStep}
     */
    @Override
    public ResponseStep connectBlock() {
        try {
            this.response = this.methodType
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new APIException(ResponseCode.INTERNAL_SERVER_ERROR, msg))))
                    .bodyToMono(Object.class)
                    .retryWhen(Retry.fixedDelay(3, java.time.Duration.ofSeconds(1))
                            .doBeforeRetry(before -> log.info("Retry: {} | {}", before.totalRetries(), before.failure())))
                    .block();
            return new ResponseStepImpl(this.response);
        } catch (Exception e) {
            log.error("Exception: {} | {}", e.getMessage(), e.getStackTrace()[0].toString());
            throw new APIException(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Mono<?> connectSubscribe(Map<String, String> headers, Class<?> responseType) {
        try {
            return this.methodType
                    .headers(httpHeaders -> httpHeaders.setAll(headers == null || headers.isEmpty() ? new HashMap<>() : headers))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new APIException(ResponseCode.INTERNAL_SERVER_ERROR, msg))))
                    .bodyToMono(responseType)
                    .retryWhen(Retry.fixedDelay(3, java.time.Duration.ofSeconds(1))
                            .doBeforeRetry(before -> log.info("Retry: {} | {}", before.totalRetries(), before.failure())));
        } catch (Exception e) {
            throw new APIException(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Mono<String> connectSubscribe() {
        try {
            return this.methodType
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new APIException(ResponseCode.INTERNAL_SERVER_ERROR, msg))))
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(3, java.time.Duration.ofSeconds(1))
                            .doBeforeRetry(before -> log.info("Retry: {} | {}", before.totalRetries(), before.failure())));
        } catch (Exception e) {
            throw new APIException(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


}

package com.zeki.kisvolcano.domain._common.web_client.builder;


import com.zeki.kisvolcano.domain._common.web_client.builder.step.MethodStep;
import com.zeki.kisvolcano.domain._common.web_client.builder.step.impl.MethodStepImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
@Component
@RequiredArgsConstructor
public class ApiWebClientBuilder {

    private final WebClient.Builder webClientBuilder;

    // 체이닝 시작점
    public <T> MethodStep<T> request() {
        return new MethodStepImpl<>(this.webClientBuilder);
    }
}

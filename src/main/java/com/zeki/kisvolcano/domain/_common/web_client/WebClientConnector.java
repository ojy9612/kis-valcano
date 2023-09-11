package com.zeki.kisvolcano.domain._common.web_client;


import com.zeki.kisvolcano.domain._common.web_client.builder.ApiWebClientBuilder;
import com.zeki.kisvolcano.domain._common.web_client.statics.ApiStatics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class WebClientConnector {
    private final ApiWebClientBuilder webClientBuilder;
    private final ApiStatics statics;

//    public void callGoogleChat(WebHookLogReqDto reqDto) {
//        webClientBuilder.request()
//                .post(reqDto.getUrl(), "", reqDto.getContent())
//                .connectBlock()
//                .toVoidCall();
//    }
}

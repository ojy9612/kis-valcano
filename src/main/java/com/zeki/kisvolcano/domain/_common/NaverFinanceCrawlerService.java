package com.zeki.kisvolcano.domain._common;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverFinanceCrawlerService {

    private final WebClientConnector webClientConnector;

    /**
     * naver finance api 를 이용해서 주가데이터를 받아온다.
     * 공식적인 경로는 아니라서 에러시 재확인 필요
     *
     * @param stockCode 종목 코드
     * @param stdDay    기준 일자
     * @return 비정형 주가 데이터 반환 ([] 형식)
     */
    @Transactional
    public String crawlStockPrice(String stockCode, LocalDate stdDay, int count) {
        /* naver api로 주가 데이터 요청 */
        String requestType = "2";
        String startTime = stdDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeframe = "day";

        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("symbol", stockCode);
        reqParam.add("requestType", requestType);
        reqParam.add("count", String.valueOf(count));
        reqParam.add("startTime", startTime);
        reqParam.add("timeframe", timeframe);

        return webClientConnector.<Void, String>connectBuilder()
                .method(HttpMethod.GET)
                .path("api.finance.naver.com/siseJson.naver")
                .requestHeaders(null)
                .requestParams(reqParam)
                .requestBody(null)
                .classType(String.class)
                .build().getBody();
    }


}

package com.zeki.kisvolcano.domain.crawl.service;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class NaverFinanceCrawlerServiceTest {
    @Autowired
    NaverFinanceCrawlerService naverFinanceCrawlerService;

    @Autowired
    WebClientConnector webClientConnector;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {
        @Test
        @DisplayName("네이버 주식 데이터 크롤링 테스트")
        void crawlStockPrice() {
            String stockCode = "005930";
            LocalDate stdDay = LocalDate.now();
            int count = 10;

            String result = naverFinanceCrawlerService.crawlStockPrice(stockCode, stdDay, count);

            int firstBracketIndex = result.indexOf('[');
            int secondBracketIndex = result.indexOf('[', firstBracketIndex + 1);
            int closingBracketIndex = result.indexOf(']', secondBracketIndex);

            String substring = result.substring(secondBracketIndex + 1, closingBracketIndex);

            String replaceAll = substring.replaceAll(" ", "");

            String[] split = replaceAll.split(",");

            assertEquals("'날짜'", split[0]);
            assertEquals("'시가'", split[1]);
            assertEquals("'고가'", split[2]);
            assertEquals("'저가'", split[3]);
            assertEquals("'종가'", split[4]);
            assertEquals("'거래량'", split[5]);
            assertEquals("'외국인소진율'", split[6]);
        }
    }
}
package com.zeki.kisvolcano.domain.kis.stock.service;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain._common.web_client.statics.ApiStatics;
import com.zeki.kisvolcano.domain.kis.stock.repository.StockInfoRepository;
import com.zeki.kisvolcano.domain.kis.token.service.TokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class StockInfoServiceTest {

    @Autowired
    StockInfoService stockInfoService;

    @Autowired
    StockInfoRepository stockInfoRepository;
    @Autowired
    WebClientConnector webClientConnector;
    @Autowired
    StockCodeService stockCodeService;
    @Autowired
    TokenService tokenService;

    @Autowired
    ApiStatics apiStatics;

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
        @DisplayName("StockInfo 엔티티 생성 & 업데이트")
        void upsertStockInfo() {
            // given
            String stockCode = "005930";
            LocalDate stdDay = LocalDate.now();
            // when
            stockInfoService.upsertStockInfo(List.of(stockCode), stdDay.minusDays(14), stdDay);

            // then

        }

    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

    }
}
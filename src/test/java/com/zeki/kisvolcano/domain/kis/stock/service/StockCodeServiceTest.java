package com.zeki.kisvolcano.domain.kis.stock.service;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain.data_go.holiday.service.HolidayService;
import com.zeki.kisvolcano.domain.kis.stock.dto.StockCodeResDto;
import com.zeki.kisvolcano.domain.kis.stock.entity.StockCode;
import com.zeki.kisvolcano.domain.kis.stock.repository.StockCodeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class StockCodeServiceTest {
    @Autowired
    StockCodeService stockCodeService;

    @Autowired
    StockCodeRepository stockCodeRepository;
    @Autowired
    HolidayService holidayService;
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
        @DisplayName("상장된 모든 종목 코드를 업데이트, 생성 한다.")
        @Transactional
        void upsertStockCode() {
            // given
            String year = Year.now().format(DateTimeFormatter.ofPattern("yyyy"));
            holidayService.createHolidaysByYear(Integer.parseInt(year));

            ExtendStockCodeServiceTest extendStockCodeServiceTest = new ExtendStockCodeServiceTest(stockCodeRepository, holidayService, webClientConnector);

            // when
            stockCodeService.upsertStockCode();
            List<StockCode> stockCodeList = stockCodeService.getStockCodeList();

            StockCode stockCode1 = stockCodeList.get(0);
            StockCode stockCode2 = stockCodeList.get(1);
            String stockName = stockCode1.getName();
            StockCodeResDto.Item item1 = StockCodeResDto.Item.builder()
                    .srtncd("A" + stockCode1.getCode())
                    .itmsnm(stockCode1.getName() + "test")
                    .mrktctg(stockCode1.getMarket())
                    .build();
            StockCodeResDto.Item item2 = StockCodeResDto.Item.builder()
                    .srtncd("A" + stockCode2.getCode())
                    .itmsnm(stockCode2.getName())
                    .mrktctg(stockCode2.getMarket())
                    .build();
            extendStockCodeServiceTest.setTestItem(List.of(item1, item2));
            extendStockCodeServiceTest.upsertStockCode();
            List<StockCode> stockCodeList2 = extendStockCodeServiceTest.getStockCodeList();

            Optional<StockCode> first = stockCodeList2.stream()
                    .filter(entity -> entity.getCode().equals(stockCode1.getCode()))
                    .findFirst();

            // then
            assertTrue(stockCodeList.size() > 2400);
            assertEquals(stockCodeList.size(), stockCodeList2.size());
            assertTrue(first.isPresent());
            assertEquals(first.get().getName(), stockName + "test");
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("data.go.kr API 호출 실패")
        void upsertStockCode() {
            // given
            ExtendStockCodeServiceTest extendStockCodeServiceTest = new ExtendStockCodeServiceTest(stockCodeRepository, holidayService, webClientConnector);
            extendStockCodeServiceTest.setTestItem(new ArrayList<>());

            // when
            extendStockCodeServiceTest.upsertStockCode();
            List<StockCode> stockCodeList = extendStockCodeServiceTest.getStockCodeList();

            // then
            assertEquals(0, stockCodeList.size());
        }
    }
}
package com.zeki.kisvolcano.domain.kis.stock.service;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain.data_go.holiday.service.HolidayService;
import com.zeki.kisvolcano.domain.kis.stock.dto.StockCodeResDto;
import com.zeki.kisvolcano.domain.kis.stock.entity.StockCode;
import com.zeki.kisvolcano.domain.kis.stock.repository.StockCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockCodeService {
    private final StockCodeRepository stockCodeRepository;
    private final HolidayService holidayService;

    private final WebClientConnector webClientConnector;

    /**
     * 상장된 모든 종목 코드를 업데이트, 생성 한다.
     */
    @Transactional
    public void upsertStockCode() {
        int pageNo = 1;
        int totalCount = Integer.MAX_VALUE;
        List<StockCodeResDto.Item> bodyList = new ArrayList<>();
        StockCodeResDto response;
        String beforeDate = holidayService.deltaTwoAvailableDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        reqParam.set("resultType", "json");
        reqParam.set("numOfRows", "1000");
        reqParam.set("basDt", beforeDate);

        while (totalCount > 1000 * (pageNo - 1)) {
            reqParam.set("pageNo", String.valueOf(pageNo));
            response = webClientConnector.<Void, StockCodeResDto>connectDataGoApiBuilder()
                    .method(HttpMethod.GET)
                    .path("1160100/service/GetKrxListedInfoService/getItemInfo")
                    .requestHeaders(null)
                    .requestParams(reqParam)
                    .requestBody(null)
                    .classType(StockCodeResDto.class)
                    .build().getBody();

            totalCount = response.getResponse().getBody().getTotalcount();
            pageNo++;
            bodyList.addAll(response.getResponse().getBody().getItems().getItem());
        }

        List<StockCode> stockCodeList = bodyList.stream().filter(item -> item.getMrktctg().equals("KOSDAQ") || item.getMrktctg().equals("KOSPI"))
                .map(body -> StockCode.builder()
                        .name(body.getItmsnm())
                        .code(body.getSrtncd().substring(1))
                        .market(body.getMrktctg())
                        .build()
                ).toList();

        for (StockCodeResDto.Item item : bodyList) {
            if (item.getMrktctg().equals("KOSDAQ") || item.getMrktctg().equals("KOSPI")) {
                String name = item.getItmsnm();
                String code = item.getSrtncd().substring(1);
                String market = item.getMrktctg();
                Optional<StockCode> stockCodeOptional = stockCodeRepository.findByCode(code);

                if (stockCodeOptional.isEmpty()) {
                    StockCode stockCode = StockCode.builder()
                            .name(name)
                            .code(code)
                            .market(market)
                            .build();
                    stockCodeRepository.save(stockCode);
                } else {
                    stockCodeOptional.get().updateStockCodeBuilder()
                            .market(market)
                            .build();
                }

            }
        }

        stockCodeRepository.saveAll(stockCodeList);
    }

    /**
     * DB에 저장된 모든 종목코드를 불러온다.
     *
     * @return List<String> 종목코드 List
     */
    @Transactional(readOnly = true)
    public List<StockCode> getStockCodeList() {
        return stockCodeRepository.findAll();
    }
}

package com.zeki.kisvolcano.domain.kis.stock.service;

import com.zeki.kisvolcano.domain._common.web_client.WebClientConnector;
import com.zeki.kisvolcano.domain.data_go.holiday.service.HolidayService;
import com.zeki.kisvolcano.domain.kis.stock.dto.StockCodeResDto;
import com.zeki.kisvolcano.domain.kis.stock.repository.StockCodeRepository;

import java.util.List;

public class ExtendStockCodeServiceTest extends StockCodeService {

    public List<StockCodeResDto.Item> testItem;

    public ExtendStockCodeServiceTest(StockCodeRepository stockCodeRepository, HolidayService holidayService, WebClientConnector webClientConnector) {
        super(stockCodeRepository, holidayService, webClientConnector);
    }

    public void setTestItem(List<StockCodeResDto.Item> testItem) {
        this.testItem = testItem;
    }

    @Override
    protected List<StockCodeResDto.Item> getStockCodeFromDataGoKr() {
        return this.testItem;
    }
}

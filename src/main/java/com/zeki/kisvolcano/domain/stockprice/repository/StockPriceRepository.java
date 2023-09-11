package com.zeki.kisvolcano.domain.stockprice.repository;

import com.zeki.kisvolcano.domain.stockprice.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}

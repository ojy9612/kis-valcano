package com.zeki.kisvolcano.domain.kis.stock.repository;

import com.zeki.kisvolcano.domain.kis.stock.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}

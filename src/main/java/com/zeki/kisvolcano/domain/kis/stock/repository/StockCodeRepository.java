package com.zeki.kisvolcano.domain.kis.stock.repository;

import com.zeki.kisvolcano.domain.kis.stock.entity.StockCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockCodeRepository extends JpaRepository<StockCode, String> {
}
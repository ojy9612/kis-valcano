package com.zeki.kisvolcano.domain.stockcode.repository;

import com.zeki.kisvolcano.domain.stockcode.entity.StockCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockCodeRepository extends JpaRepository<StockCode, Integer> {
    Optional<StockCode> findByCode(String code);
}

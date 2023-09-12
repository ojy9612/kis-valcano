package com.zeki.kisvolcano.domain.kis.stock.repository;

import com.zeki.kisvolcano.domain.kis.stock.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {

    List<StockInfo> findByCodeIn(List<String> codeList);
}

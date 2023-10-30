package com.zeki.kisvolcano.domain.kis.stock.repository.bulk;

import com.zeki.kisvolcano.domain.kis.stock.entity.StockInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockInfoBulkRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;


    public void saveAll(List<StockInfo> items) {
        while (!items.isEmpty()) {
            int currentBatchSize = Math.min(BATCH_SIZE, items.size());
            List<StockInfo> subItems = items.subList(0, currentBatchSize);
            this.batchInsert(subItems);
            items.subList(0, currentBatchSize).clear();
        }
    }

    private void batchInsert(List<StockInfo> subItems) {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO stock_info (name, code, other_code, fcam, amount, market_capitalization, capital, per, pbr, eps, created_at, updated_at)");
        sb.append(" VALUES ");

        for (int i = 0; i < subItems.size(); i++) {
            sb.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        int countColumn = 10;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sb.toString());

            for (int i = 0; i < subItems.size(); i++) {
                StockInfo entity = subItems.get(i);
                ps.setString(i * countColumn + 1, entity.getName());
                ps.setString(i * countColumn + 2, entity.getCode());
                ps.setString(i * countColumn + 3, entity.getOtherCode());
                ps.setString(i * countColumn + 4, entity.getFcam());
                ps.setLong(i * countColumn + 5, entity.getAmount());
                ps.setString(i * countColumn + 6, entity.getMarketCapitalization());
                ps.setString(i * countColumn + 7, entity.getCapital());
                ps.setString(i * countColumn + 8, entity.getPer());
                ps.setString(i * countColumn + 9, entity.getPbr());
                ps.setString(i * countColumn + 10, entity.getEps());
            }

            return ps;
        });
    }

    public void updateAll(List<StockInfo> updateBlockInfoList) {
        while (!updateBlockInfoList.isEmpty()) {
            int currentBatchSize = Math.min(BATCH_SIZE, updateBlockInfoList.size());
            List<StockInfo> subItems = updateBlockInfoList.subList(0, currentBatchSize);
            this.batchUpdate(subItems);
            updateBlockInfoList.subList(0, currentBatchSize).clear();
        }
    }

    private void batchUpdate(List<StockInfo> subItems) {
        String sql = "UPDATE stock_info " +
                "SET other_code = ?, fcam = ?, amount = ?, market_capitalization = ?, capital = ?, per = ?, pbr = ?, eps = ?, updated_at = now() " +
                "WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StockInfo entity = subItems.get(i);
                ps.setString(1, entity.getOtherCode());
                ps.setString(2, entity.getFcam());
                ps.setLong(3, entity.getAmount());
                ps.setString(4, entity.getMarketCapitalization());
                ps.setString(5, entity.getCapital());
                ps.setString(6, entity.getPer());
                ps.setString(7, entity.getPbr());
                ps.setString(8, entity.getEps());
                ps.setLong(10, entity.getId());
            }

            @Override
            public int getBatchSize() {
                return subItems.size();
            }
        });
    }
}

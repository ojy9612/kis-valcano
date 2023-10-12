package com.zeki.kisvolcano.domain.kis.stock.entity;

import com.zeki.kisvolcano.domain._common.utils.TimeStamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "stock_code")
public class StockCode extends TimeStamped {
    @Id
    @Size(max = 20)
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 10)
    @NotNull
    @Column(name = "market", nullable = false, length = 10)
    private String market;

    @Builder
    public StockCode(String code, String name, String market) {
        this.code = code;
        this.name = name;
        this.market = market;
    }

    @Builder(builderMethodName = "updateStockCodeBuilder", builderClassName = "StockCodeUpdateBuilder")
    public boolean updateStockCode(String name, String market) {
        if (this.name.equals(name) && this.market.equals(market))
            return false;
        
        this.name = name;
        this.market = market;

        return true;
    }
}
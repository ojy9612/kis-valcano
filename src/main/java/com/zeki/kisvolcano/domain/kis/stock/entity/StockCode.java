package com.zeki.kisvolcano.domain.kis.stock.entity;

import com.zeki.kisvolcano.domain._common.utils.TimeStamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "stock_code", uniqueConstraints = {
        @UniqueConstraint(name = "UK_stock_info_name", columnNames = {"name"}),
        @UniqueConstraint(name = "UK_stock_info_code", columnNames = {"code"})
}, indexes = {
        @Index(name = "code_index", columnList = "code")
})
public class StockCode extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    @Comment("종목 이름")
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    @Comment("종목 코드")
    private String code;

    @Column(nullable = false, length = 10)
    @Comment("시장 이름 (ex. 코스피, 코스닥, 코넥스)")
    private String market;

    @Builder
    public StockCode(String name, String code, String market) {
        this.name = name;
        this.code = code;
        this.market = market;
    }

    @Builder(builderMethodName = "updateStockCodeBuilder")
    public void updateStockCode(String market) {
        this.market = market;
    }

}

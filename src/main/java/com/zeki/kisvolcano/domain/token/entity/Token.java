package com.zeki.kisvolcano.domain.token.entity;


import com.zeki.kisvolcano.domain._common.em.TradeMode;
import com.zeki.kisvolcano.domain._common.utils.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Token extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    @Comment("토큰")
    private String tokenValue;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @Comment("매매 모드 (ex. 모의투자, 실전투자)")
    private TradeMode mode;

    @Column(nullable = false)
    @Comment("만료 일자")
    private LocalDateTime expiredDate;


    @Builder
    public Token(String tokenValue, TradeMode mode, LocalDateTime expiredDate) {
        this.tokenValue = tokenValue;
        this.mode = mode;
        this.expiredDate = expiredDate;
    }
}

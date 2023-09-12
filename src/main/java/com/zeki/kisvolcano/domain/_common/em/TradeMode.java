package com.zeki.kisvolcano.domain._common.em;

import com.zeki.kisvolcano.exception.APIException;
import com.zeki.kisvolcano.exception.ResponseCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum TradeMode {
    REAL("real"),
    TRAIN("train");

    private String name;

    public static TradeMode getTradeMode(String name) {
        for (TradeMode tradeMode : values()) {
            if (tradeMode.getName().equalsIgnoreCase(name)) {
                return tradeMode;
            }
        }
        throw new APIException(ResponseCode.FAIL_TO_READ_PROFILE,"유효하지 않은 TradeMode 입니다. - " + name);
    }

    TradeMode(String name) {
        this.name = name;
    }
}

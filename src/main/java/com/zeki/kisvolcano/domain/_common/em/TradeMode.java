package com.zeki.kisvolcano.domain._common.em;

import com.zeki.kisvolcano.exception.APIException;
import com.zeki.kisvolcano.exception.ResponseCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
public enum TradeMode implements DescriptionEnum {
    REAL("prod"),
    TRAIN("dev"),
    TEST("test");

    private String description;

    TradeMode(String name) {
        this.description = name;
    }

    public static @NotNull TradeMode getEnum(@NotNull String description) {
        for (TradeMode tradeMode : values()) {
            if (tradeMode.getDescription().equalsIgnoreCase(description)) {
                return tradeMode;
            }
        }
        throw new APIException(ResponseCode.INVALID_PROFILE, "유효하지 않은 TradeMode 입니다. - " + description);
    }
}

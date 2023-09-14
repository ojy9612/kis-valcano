package com.zeki.kisvolcano.domain._common.em;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public enum OrderType implements DescriptionEnum {

    BUY("BUY"),
    SELL("SELL");

    private String description;

    OrderType(String description) {
        this.description = description;
    }

    public static @Nullable OrderType getEnum(@NotNull String description) {
        for (OrderType tradeMode : values()) {
            if (tradeMode.getDescription().equalsIgnoreCase(description)) {
                return tradeMode;
            }
        }
        return null;
    }
}

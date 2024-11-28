package io.reactivestax.utility;

import io.reactivestax.types.dto.Trade;
import io.reactivestax.types.enums.Direction;
import jakarta.persistence.Enumerated;


public class Utility {

    public static boolean checkValidity(String[] split) {
        return (split[0] != null && split[1] != null && split[2] != null && split[3] != null && split[4] != null && split[5] != null && split[6] != null);
    }

    public static Trade prepareTrade(String payload) {
        String[] payloads = payload.split(",");

        return Trade.builder()
                .tradeIdentifier(payloads[0])
                .tradeDateTime(payloads[1])
                .accountNumber(payloads[2])
                .cusip(payloads[3])
                .direction(Direction.valueOf(payloads[4]))
                .quantity(Integer.parseInt(payloads[5]))
                .price(Double.parseDouble(payloads[6]))
                .position(Integer.parseInt(payloads[5]))
                .build();

    }


}

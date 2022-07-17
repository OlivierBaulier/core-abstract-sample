package com.example.shop.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;


import java.math.BigInteger;
import javax.validation.constraints.NotNull;

/**
 * DTO of stock movement of shoe boxes
 *
 *  A negative quantity for the stock inflows.
 *  A positive quantity for the stock outflows.
 */
@Data
@Builder
public class StockMovement implements  Comparable<StockMovement>{

    @NotNull(message = "size shouldn't be null")
    @Schema(example = "40")
    @Getter BigInteger size;
    @NotNull(message = "color shouldn't be null")
    @Schema(example = "BLACK")
    @Getter String color;
    @Schema(example = "10")
    @NotNull(message = "quantity shouldn't be null")
    @Getter  int quantity;

    @Override
    public int compareTo(StockMovement other) {
        int result;

        result = this.getColor().compareTo(other.getColor());
        if(result == 0){
            result = this.getSize().compareTo(other.getSize());
            if(result == 0){
                result = this.getQuantity() - other.getQuantity();
            }
        }

        return result;
    }
}
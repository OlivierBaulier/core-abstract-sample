package com.example.demo.dto.in;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


import java.math.BigInteger;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class StockMovement implements  Comparable<StockMovement>{

    @NotNull(message = "size shouldn't be null")
    @Getter BigInteger size;
    @NotNull(message = "color shouldn't be null")
    @Getter String color;
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

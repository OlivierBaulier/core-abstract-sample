package com.example.demo.dto.in;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
public class StockMovement implements  Comparable<StockMovement>{

    @Getter BigInteger size;
    @Getter String color;
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

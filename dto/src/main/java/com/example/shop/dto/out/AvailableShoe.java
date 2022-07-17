package com.example.shop.dto.out;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
@JsonDeserialize(builder = AvailableShoe.AvailableShoeBuilder.class)
public class AvailableShoe  implements Comparable<AvailableShoe>{


    @Schema( example = "BLACK")
    String color;
    @Schema(example = "40")
    BigInteger size;
    @Schema(example = "10")
    int quantity;


    @JsonPOJOBuilder(withPrefix = "")
    public static class AvailableShoeBuilder {

    }

    public AvailableShoe(String color, BigInteger size, int quantity) {
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }

    @Override
    public int compareTo(AvailableShoe other) {
        int result;
        result = this.getColor().compareTo(other.getColor());
        if(result == 0){
            result = this.getSize().compareTo(other.getSize());
        }
        return result;
    }
}

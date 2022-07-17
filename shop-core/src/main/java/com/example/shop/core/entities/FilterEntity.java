package com.example.shop.core.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Builder
public class FilterEntity implements Comparable<FilterEntity>{

    @Getter  @Setter
    private String Color;

    @Getter @Setter
    private BigInteger size;

    public FilterEntity(String color, BigInteger size) {
        Color = color;
        this.size = size;
    }

    @Override
    public int compareTo(FilterEntity other) {
        int result = this.getColor().compareTo(other.getColor());
        if(result == 0){
            result = this.getSize().compareTo(other.getSize());
        }
        return result;
    }
}

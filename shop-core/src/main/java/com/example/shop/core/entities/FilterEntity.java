package com.example.shop.core.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Builder
public class FilterEntity implements Comparable<FilterEntity>{

    @Getter  @Setter
    private String name;

    @Getter  @Setter
    private String color;

    @Getter @Setter
    private BigInteger size;

    public FilterEntity(String name,String color, BigInteger size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public int compareTo(FilterEntity other) {
        int result = this.getColor().compareTo(other.getColor());
        if(result == 0){
            result = this.getSize().compareTo(other.getSize());
            if(result == 0){
                result = this.getName().compareTo(other.getName());
            }
        }
        return result;
    }
}

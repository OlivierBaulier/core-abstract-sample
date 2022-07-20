package com.example.shop.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;


import java.util.Optional;

@Value
@Builder
public class ModelFilter implements Comparable<ModelFilter> {

    @Schema(example = "Shop shoe")
    String name;
    @Schema(example = "BLACK")
    String color;
    @Schema(example = "40")
    Integer size;

    public ModelFilter(String name, String color, Integer size) {
        this.name = name;
        this.color = color;
        this.size = size;
    }

    /*
    public Optional<Integer> getSize(){
        return Optional.ofNullable(size);
    }

    public Optional<String> getColor(){
        return Optional.ofNullable(color);
    }

    public Optional<String> getName(){
        return Optional.ofNullable(name);
    }
    */

    @Override
    public int compareTo(ModelFilter other) {
        int result = this.color.compareTo(other.color);
        if(result == 0){
            result = this.size.compareTo(other.size);
            if(result == 0){
                result = this.name.compareTo(other.name);
            }
        }
        return result;
    }
}


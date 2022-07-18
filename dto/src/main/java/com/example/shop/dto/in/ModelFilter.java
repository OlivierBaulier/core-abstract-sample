package com.example.shop.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class ModelFilter {

    @Schema(example = "Shop shoe")
    @Setter
    String name;
    @Schema(example = "BLACK")
    @Setter
    String color;
    @Schema(example = "40")
    @Setter
    Integer size;

    public ModelFilter(String name, String color, Integer size) {
        this.name = name;
        this.color = color;
        this.size = size;
    }


    public Optional<Integer> getSize(){
        return Optional.ofNullable(size);
    }

    public Optional<String> getColor(){
        return Optional.ofNullable(color);
    }

    public Optional<String> getName(){
        return Optional.ofNullable(name);
    }

}


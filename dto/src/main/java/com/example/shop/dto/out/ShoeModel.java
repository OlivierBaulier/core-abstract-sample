package com.example.shop.dto.out;

import com.example.shop.dto.out.ShoeModel.ShoeModelBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = ShoeModelBuilder.class)
public class ShoeModel implements Comparable<ShoeModel>{


    @Schema(example = "Shop shoe")
    Integer     model_id;
    @Schema(example = "Shop shoe")
    String     name;
    @Schema(example = "39")
    Integer size;
    @Schema(example = "BLACK")
    String color;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ShoeModelBuilder {

    }

    @Override
    public int compareTo(ShoeModel other) {
        int result;
        result = this.getName().compareTo(other.getName());
        if( result == 0) {
            result = this.getColor().compareTo(other.getColor());
            if(result == 0){
                result = this.getSize().compareTo(other.getSize());
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object other){
        if(other.getClass() != this.getClass()) {
            return false;
        } else {
            return (this.compareTo((ShoeModel)other) == 0);
        }
    }

}

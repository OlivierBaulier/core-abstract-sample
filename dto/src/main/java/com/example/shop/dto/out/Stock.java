package com.example.shop.dto.out;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = Stock.StockBuilder.class)
public class Stock {

    public Stock(State state, List<AvailableShoe> shoes)  {
        this.state = state;
        this.shoes = shoes;
    }

    public enum State {
        EMPTY,
        FULL,
        SOME
    }

    public State     state;
    public List<AvailableShoe> shoes;

    @JsonPOJOBuilder(withPrefix = "")
    public static class StockBuilder {

    }

}

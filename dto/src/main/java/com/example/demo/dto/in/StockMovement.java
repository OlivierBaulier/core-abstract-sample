package com.example.demo.dto.in;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
public class StockMovement {

    @Getter BigInteger size;
    @Getter String color;
    @Getter  int quantity;
}

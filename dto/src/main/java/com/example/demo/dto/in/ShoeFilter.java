package com.example.demo.dto.in;

import java.math.BigInteger;
import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class ShoeFilter {

  @Schema(example = "40")
  BigInteger size;
  Color color;

  public enum Color{

    BLACK,
    BLUE,
    ;

  }

  public Optional<BigInteger> getSize(){
    return Optional.ofNullable(size);
  }

  public Optional<Color> getColor(){
    return Optional.ofNullable(color);
  }

}

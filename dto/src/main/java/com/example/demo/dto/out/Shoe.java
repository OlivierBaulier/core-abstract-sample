package com.example.demo.dto.out;

import com.example.demo.dto.in.ShoeFilter.Color;
import com.example.demo.dto.out.Shoe.ShoeBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = ShoeBuilder.class)
public class Shoe implements Comparable<Shoe>{

  String     name;
  BigInteger size;
  Color      color;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ShoeBuilder {

  }

  @Override
  public int compareTo(Shoe other) {
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
}

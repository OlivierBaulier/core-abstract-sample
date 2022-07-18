package com.example.shop.dto.out;

import com.example.shop.dto.out.Catalog.CatalogBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = CatalogBuilder.class)
public class Catalog {


    List<ShoeModel> shoes;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CatalogBuilder {

    }
}

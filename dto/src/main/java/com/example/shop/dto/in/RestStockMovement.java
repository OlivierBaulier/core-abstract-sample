package com.example.shop.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/** DTO used to transport Stock movement over REST API
 *
 */
@Data
@Builder
public class RestStockMovement implements  Comparable<RestStockMovement>{

    @NotNull(message = "name shouldn't be null")
    @Schema(example = "0")
    @Getter
    Integer model_id;
    @Schema(example = "10")
    @NotNull(message = "quantity shouldn't be null")
    @Getter  int quantity;

    @Override
    public int compareTo(RestStockMovement other) {
        int result;

        result = this.getModel_id() -  other.model_id;
        if(result == 0){
            return this.getQuantity() - other.getQuantity();
        }
        return result;
    }
}


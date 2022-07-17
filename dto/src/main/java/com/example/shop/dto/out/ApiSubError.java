package com.example.shop.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/** Atomic error holding the context of issuer, that allows the presentation layer to perform i18n task.
 *
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSubError {
    @Schema( example = "javax.validation.constraints.NotNull")
    String code;
    @Schema( example ="color shouldn't be null")
    String message;
    @Schema( example ="{\n" +
            "                \"size\": 40,\n" +
            "                \"color\": null,\n" +
            "                \"quantity\": -40\n" +
            "            }")
    Object context;

    public ApiSubError(){}

    public ApiSubError(String code, String message, Object context) {
        this();
        this.message = message;
        this.code = code;
        this.context = context;
    }
}
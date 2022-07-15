package com.example.shop.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Atomic error holding the context of issuer, that allows the presentation perform i18n stack.
 *
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSubError {
    String code;
    String message;
    Object context;

    public ApiSubError(String code, String message, Object context) {
        this.message = message;
        this.code = code;
        this.context = context;
    }
}
package com.example.shop.core;

import com.example.shop.core.ShopException;

public class NotFoundException extends ShopException {
    /** Throw when en record is not found in persistance layer
     *
     * @param field of the rout cause of exception
     * @param message default diagnostic to i18n
     * @param ctx context of issue      * @param ctx context of issue allowing build custom message (i18n)
     */
    public NotFoundException(String field, String message, Object ctx){
        super(field, message, ctx);
    }

}

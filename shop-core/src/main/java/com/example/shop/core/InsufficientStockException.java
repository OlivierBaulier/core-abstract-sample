package com.example.shop.core;

/** Throws in case of the stock is insufficient stock for the requested quantity
 *
 * contains the context of exception allowing presentation layer to customize message
 */
public class InsufficientStockException extends ShopException {
    public InsufficientStockException(String field, String message, Object ctx){
        super(field, message, ctx);
    }
}

package com.example.shop.core;

/** Throws in case of the stock update exceeds the shop capacity
 *
 * contains the context of exception allowing presentation layer to customize message
 */
public class CapacityReachedException extends ShopException {

    public CapacityReachedException(String field, String message, Object ctx){
        super(field, message, ctx);
    }
}

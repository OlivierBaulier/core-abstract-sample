package com.example.shop.core;

/** Throws in case of the stock update exceeds the shop capacity
 *
 * contains the context of exception allowing presentation layer to customize message
 */
public class CapacityReachedException extends ShopException {

    /** Not enough space to perform this operation
     *
     * @param field of the route cause of exception
     * @param message default message to in18
     * @param ctx context of issue allowing build custom message (i18n)
     */
    public CapacityReachedException(String field, String message, Object ctx){
        super(field, message, ctx);
    }
}

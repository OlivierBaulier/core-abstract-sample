package com.example.shop.core;

/** Throws in case of the stock is insufficient stock for the requested quantity
 *
 * contains the context of exception allowing presentation layer to customize message
 */
public class InsufficientStockException extends ShopException {
    /** Not enough ressource for this operation
     *
     * @param field of the route cause of exception
     * @param message default message to in18
     * @param ctx context of issue allowing build custom message (i18n)
     */
    public InsufficientStockException(String field, String message, Object ctx){
        super(field, message, ctx);
    }
}

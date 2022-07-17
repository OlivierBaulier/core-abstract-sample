package com.example.shop.core;

import com.example.shop.core.entities.FilterEntity;
import com.example.shop.dto.in.StockMovement;
import com.example.demo.dto.in.ShoeFilter;
import com.example.shop.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;

import java.util.List;

/** Database interface for the shoe Shop.
 *
 */
public interface DatabaseAdapter {


    /** To get the shoe catalog
     *
     * @param filter the filter to apply for the research
     * @return the available shoe model in catalog
     */
    List<Shoe> getCatalog(ShoeFilter filter);

    // to get the current stock state

    /** to get the current stock state without shoe model filter
     *
     * @return the stock descriptor of available shoes.
     */
    List<AvailableShoe> getStock();


    /** to get the current stock state with a shoe model filter
     *
     * @param filter the filter to apply
     * @return the stock descriptor of available shoes.
     */
    List<AvailableShoe> getStockWithFilter(FilterEntity filter);

    /** to get the number of available shoe boxes with filter
     *
     * @param filter the filter to apply
     * @return the number of available she boxes
     */
    int countShoes(FilterEntity filter);

    /** to perform an inflow movement in the shop stock
     *
     * @param movement the inflow movement to perform, the movement quality must be positive
     * @return the number of shoes boxes effectively moved
     */
    int stock(StockMovement movement);

    /** to perform an outflow movement in the shop stock
     *
     * @param movement the inflow movement to perform, the movement quality must be negative
     * @return the number of shoes boxes effectively moved
     */
    int destock(StockMovement movement);
}

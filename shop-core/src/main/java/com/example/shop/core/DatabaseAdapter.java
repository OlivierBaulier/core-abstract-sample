package com.example.shop.core;

import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.AvailableShoe;
import com.example.shop.dto.out.ShoeModel;

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
    List<ShoeModel> getCatalog(ModelFilter filter);

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
    List<AvailableShoe> getStockWithFilter(ModelFilter filter);

    /** to get the number of available shoe boxes with filter
     *
     * @param filter the filter to apply
     * @return the number of available she boxes
     */
    int countShoes(ModelFilter filter);

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

    /**
     * Add new shoe model in shop catalog if not already exist
     * @param size of the color of the shoe model
     * @param name of the color of the shoe model
     * @param color of the color of the shoe model
     * @return return the ID of the corresponding shoeModel
     */
    int addModelIfNotExist(String name, String color, int size);


    /** get Shoe Model by its model_id
     *
     * @param model_id the identifier of Shoe Model
     * @return the wanted Shoe Model or throw NotFountException exception
     */
    ShoeModel getShoeModelById(int model_id);

}

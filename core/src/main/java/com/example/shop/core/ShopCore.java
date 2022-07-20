package com.example.shop.core;

import com.example.demo.dto.in.ShoeFilter;
import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.RestStockMovement;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.Catalog;
import com.example.shop.dto.out.ShoeModel;
import com.example.shop.dto.out.Stock;

import java.util.List;

public interface ShopCore  {

    /** Used to consult the shoe catalog of the shop
     *
     * @param filter  To limit the research to wanted models
     * @return  the list of the models available models
     */
    Catalog catalog(ModelFilter filter);

    Stock getStock();

    /** Old version of Update shoe stock
     *
     * @param movements The list of movements to update
     * @return The sum of quantity of each mouvement if all are taken
     */
    Integer stockUpdate(List<StockMovement> movements) throws Exception;



    /** Add or get a shoe Model if already exist
     *
     * @param shoe The Shoe model to create
     * @return Shoe Model Id
     */
    Integer addOrUpdateShoeModel(ShoeModel shoe);

    /** Get Shoe Model by its model_id
     *
     * @param model_id the id of the model to get
     * @return   return the model_id of corresponding resource
     */
    ShoeModel getShoeModelById(int model_id);

    /** REST standard method to update Shoes Stock
     *
     * @param movements The list of movements to update
     * @return The sum of quantity of each mouvement if all are taken
     */
    Integer restStockUpdate(List<RestStockMovement> movements);

}

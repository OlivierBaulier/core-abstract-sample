package com.example.shop.core;

import com.example.demo.core.AbstractShopCore;
import com.example.demo.core.Implementation;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;
import com.example.demo.dto.out.Shoes;
import com.example.demo.dto.out.Stock;
import com.example.shop.core.entities.FilterEntity;
import org.modelmapper.internal.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@ComponentScan
@Implementation(version = 3)
public class ShopCoreImpl extends AbstractShopCore {

    int MX_CAPACITY = 30;
    @Autowired
    private DatabaseAdapter databaseAdapter;

    @Override
    public Shoes search(ShoeFilter filter) {
        List<Shoe> shoes = this.databaseAdapter.getCatalog(filter);
        return  Shoes.builder().shoes(shoes).build();
    }


    @Override
    public Stock getStock() {
        List<AvailableShoe> stock = this.databaseAdapter.getStock();
        int totalCount = stock.stream().map(AvailableShoe::getQuantity).reduce(0, Integer::sum);
        Stock.State state;
        if (totalCount <= 0) {
            state = Stock.State.EMPTY;
        } else if (totalCount >= this.MX_CAPACITY) {
            state = Stock.State.FULL;
        } else {
            state = Stock.State.SOME;
        }
        return Stock.builder().state(state).shoes(stock).build();
    }


    /** Manage stock update in multi line stratégie
     * The strategy used is to start by adding movement first
     *
     * @param movements the movements to apply to stock
     * @return the balance of shoes boxes after all updates
     * @throws Exception
     */
    public int stockUpdateMultiLine(StockMovement[] movements) throws Exception {
        int result = 0;
        // Sort movements to have addition in first
        List<StockMovement> insertionOrder = Arrays.asList(movements);
        Collections.sort(insertionOrder, (a,b) -> b.compareTo(a) );
        // performs the movements in correct order
        for(StockMovement mvt :insertionOrder)
        {
            int resultMvt = 0;
            int mvtDirection = 1;
            if(mvt.getQuantity() > 0){
                resultMvt = this.databaseAdapter.stock(mvt);
            } else if (mvt.getQuantity() < 0){
                mvtDirection = -1;
                resultMvt =  this.databaseAdapter.destock(mvt);
           }
            // check the movement is done completely
            if(resultMvt != mvt.getQuantity()){
                throw new Exception(String.format("Transaction error : (Expected %d : result %d)", mvtDirection * mvt.getQuantity(), resultMvt));
            }
            result += resultMvt;
        }
        int newStockCount = this.databaseAdapter.countShoes( new FilterEntity(null, null));
        // check if limit is reached
        if(newStockCount > MX_CAPACITY){
            throw new Exception(String.format("The quantity reaches the capacity limit of the shop : (stock %d : sum of movements  %d)", newStockCount, result ));
        }
        return result;
    }

    @Override
    public int stockUpdate(StockMovement[] movements) throws Exception {
        if (movements.length == 1){
            return stockUpdateSingleLine( movements[0]);
        }else{
            return this.stockUpdateMultiLine(movements);
        }
    }

    /** App
     *
     * @param movement
     * @return
     * @throws Exception
     */
    int stockUpdateSingleLine(StockMovement movement) throws Exception {
        int result = 0;
        if( movement.getQuantity() < 0 ) {
            int availableShoes = this.databaseAdapter.countShoes( new FilterEntity( movement.getColor(), movement.getSize()));
            int expectedResult = availableShoes +  movement.getQuantity();
            if( expectedResult < 0 ){
                throw new Exception(String.format("Not enough shoes : (stock %d : Requested %d)", availableShoes, -movement.getQuantity() ));
            }else{
                result = this.databaseAdapter.destock(movement);
                if( result !=  movement.getQuantity()) {
                    throw new Exception(String.format("Transaction error : (Expected %d : result %d)", -movement.getQuantity(), result ));
                }
            }
        } else if( movement.getQuantity() > 0 ) {
            int availableShoes = this.databaseAdapter.countShoes( new FilterEntity(null, null));
            int expectedResult = availableShoes + movement.getQuantity();
            if(expectedResult > MX_CAPACITY){
                throw new Exception(String.format("The quantity reaches the capacity limit of the shop : (stock %d : Requested %d)", availableShoes, -movement.getQuantity() ));
            } else {
                result = this.databaseAdapter.stock(movement);
                if (result != movement.getQuantity()) {
                    throw new Exception(String.format("Transaction error : (Expected %d : result %d)", -movement.getQuantity(), result));
                }
            }
        }
        return result;
    }

}

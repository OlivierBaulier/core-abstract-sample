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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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


    @Override
    public int stockUpdate(StockMovement[] movements) throws Exception {
        int result = 0;
        for(StockMovement mvnt : movements){
            result += stockUpdate(mvnt);
        }
        return result;
    }

    int stockUpdate(StockMovement movement) throws Exception {
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

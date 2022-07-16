package com.example.shop.core;

import com.example.demo.core.Implementation;
import com.example.demo.dto.in.ShoeFilter;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;
import com.example.demo.dto.out.Shoes;
import com.example.shop.dto.out.Stock;
import com.example.shop.core.entities.FilterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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
            if(resultMvt != mvt.getQuantity()) {
                if (mvtDirection < 0) {
                    throw new InsufficientStockException("stockMovement",
                            String.format("Not enough stock : (stock %d : Requested %d)", result, -mvt.getQuantity()),
                            Map.ofEntries(
                                    Map.entry("availableShoes", Integer.valueOf(resultMvt)),
                                    Map.entry("movement", mvt)
                            )
                    );
                } else {
                    throw new CapacityReachedException("stockMovement",
                            String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", result, mvt.getQuantity()),
                            Map.ofEntries(
                                    Map.entry("free places", Integer.valueOf(resultMvt)),
                                    Map.entry("movement", mvt)
                            )
                    );
                }
            }
            result += resultMvt;
        }
        int newStockCount = this.databaseAdapter.countShoes( new FilterEntity(null, null));
        // check if limit is reached
        if(newStockCount > MX_CAPACITY){
            int freePlaces = MX_CAPACITY -(newStockCount-result);
            throw new CapacityReachedException("stockMovements",
                    String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", freePlaces, result),
                    Map.ofEntries(
                            Map.entry("free places", Integer.valueOf(freePlaces)),
                            Map.entry("movement", movements)
                    )
            );
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
                throw new InsufficientStockException("stockMovement",
                        String.format("Not enough stock : (stock %d : Requested %d)", availableShoes, -movement.getQuantity() ),
                        Map.ofEntries(
                                Map.entry("availableShoes", Integer.valueOf(availableShoes)),
                                Map.entry("movement", movement)
                        )
                );
            }else{
                result = this.databaseAdapter.destock(movement);
                if( result != movement.getQuantity()) {
                    throw new InsufficientStockException("stockMovement",
                            String.format("Not enough stock : (stock %d : Requested %d)", result, -movement.getQuantity()),
                            Map.ofEntries(
                                    Map.entry("availableShoes", result),
                                    Map.entry("movement", movement)
                            )
                    );
                }
            }
        } else if( movement.getQuantity() > 0 ) {
            int availableShoes = this.databaseAdapter.countShoes( new FilterEntity(null, null));
            int expectedResult = availableShoes + movement.getQuantity();
            if(expectedResult > MX_CAPACITY){
                throw new CapacityReachedException("stockMovement",
                        String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", MX_CAPACITY-availableShoes, movement.getQuantity() ),
                        Map.ofEntries(
                                Map.entry("freePlaces", Integer.valueOf(MX_CAPACITY - availableShoes)),
                                Map.entry("movement", movement)
                        )
                );
            } else {
                result = this.databaseAdapter.stock(movement);
                if (result != movement.getQuantity()) {
                    throw new CapacityReachedException("stockMovement",
                            String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", result, movement.getQuantity() ),
                            Map.ofEntries(
                                    Map.entry("freePLaces", Integer.valueOf(result)),
                                    Map.entry("movement", movement)
                            )
                    );
                }
            }
        }
        return result;
    }

}

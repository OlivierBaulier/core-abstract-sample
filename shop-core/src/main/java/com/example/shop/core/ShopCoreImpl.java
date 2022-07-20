package com.example.shop.core;

import com.example.demo.core.Implementation;
import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.RestStockMovement;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.AvailableShoe;
import com.example.shop.dto.out.Catalog;
import com.example.shop.dto.out.ShoeModel;
import com.example.shop.dto.out.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ComponentScan
@Implementation(version = 3)
public class ShopCoreImpl extends AbstractShopCore {

    int MX_CAPACITY = 30;
    @Autowired
    private DatabaseAdapter databaseAdapter;

    @Override
    public Catalog catalog(ModelFilter filter) {
        List<ShoeModel> shoes = this.databaseAdapter.getCatalog(filter);
        return  Catalog.builder().shoes(shoes).build();
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
     */
    public int stockUpdateMultiLine(List<StockMovement> movements) {
        int result = 0;
        // Sort movements to have addition in first
        List<StockMovement> insertionOrder = movements.stream().sorted((a, b) -> b.compareTo(a)).collect(Collectors.toList());
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
                                    Map.entry("availableShoes", resultMvt),
                                    Map.entry("movement", mvt)
                            )
                    );
                } else {
                    throw new CapacityReachedException("stockMovement",
                            String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", result, mvt.getQuantity()),
                            Map.ofEntries(
                                    Map.entry("free places", resultMvt),
                                    Map.entry("movement", mvt)
                            )
                    );
                }
            }
            result += resultMvt;
        }
        int newStockCount = this.databaseAdapter.countShoes( new ModelFilter(null,null, null));
        // check if limit is reached
        if(newStockCount > MX_CAPACITY){
            int freePlaces = MX_CAPACITY -(newStockCount-result);
            throw new CapacityReachedException("stockMovements",
                    String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", freePlaces, result),
                    Map.ofEntries(
                            Map.entry("free places", freePlaces),
                            Map.entry("movement", movements)
                    )
            );
        }
        return result;
    }

    @Override
    public Integer stockUpdate(List<StockMovement> movements)  {
        if (movements.size() == 1){
            return stockUpdateSingleLine( movements.get(0));
        }else{
            return this.stockUpdateMultiLine(movements);
        }
    }


    @Override
    public Integer addOrUpdateShoeModel(ShoeModel shoe) {
        return this.databaseAdapter.addModelIfNotExist(shoe.getName(), shoe.getColor(), shoe.getSize());
    }

    @Override
    public ShoeModel getShoeModelById(int model_id) {
        return this.databaseAdapter.getShoeModelById(model_id );
    }

    @Override
    public Integer restStockUpdate(List<RestStockMovement> movements) {
        ShoeModelRepo tempRepo = new ShoeModelRepo(
                this.databaseAdapter.getCatalog(null)
        );
        List<StockMovement> stockMovement = movements.stream().map( (movement) -> {
                ShoeModel model = tempRepo.getModelById(movement.getModel_id());
                if( model == null) {
                    throw new NotFoundException(
                            "List<RestSTockMovement>(*).model_id",
                            String.format("Shoe model not found : id = \"%d\"", movement.getModel_id()),
                            Map.of("movement", movement));
                }
                return  StockMovement.builder().name(model.getName())
                        .color(model.getColor())
                        .size(BigInteger.valueOf(model.getSize()))
                        .quantity(movement.getQuantity()).build();
                }).collect(Collectors.toList());
        return this.stockUpdateMultiLine(stockMovement);
    }

    /** Single line version of stock update
     *
     * @param movement to apply to stock
     * @return Sum of all movements
     */
    int stockUpdateSingleLine(StockMovement movement)  {
        int result = 0;
        if( movement.getQuantity() < 0 ) {
            int availableShoes = this.databaseAdapter.countShoes( new ModelFilter(  movement.getName(),movement.getColor(), movement.getSize().intValue()));
            int expectedResult = availableShoes +  movement.getQuantity();
            if( expectedResult < 0 ){
                throw new InsufficientStockException("stockMovement",
                        String.format("Not enough stock : (stock %d : Requested %d)", availableShoes, -movement.getQuantity() ),
                        Map.ofEntries(
                                Map.entry("availableShoes", availableShoes),
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
            int availableShoes = this.databaseAdapter.countShoes( new ModelFilter(null,null, null));
            int expectedResult = availableShoes + movement.getQuantity();
            if(expectedResult > MX_CAPACITY){
                throw new CapacityReachedException("stockMovement",
                        String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", MX_CAPACITY-availableShoes, movement.getQuantity() ),
                        Map.ofEntries(
                                Map.entry("freePlaces", MX_CAPACITY - availableShoes),
                                Map.entry("movement", movement)
                        )
                );
            } else {
                result = this.databaseAdapter.stock(movement);
                if (result != movement.getQuantity()) {
                    throw new CapacityReachedException("stockMovement",
                            String.format("The quantity reaches the capacity limit of the shop : (free places %d : addition %d)", result, movement.getQuantity() ),
                            Map.ofEntries(
                                    Map.entry("freePLaces", result),
                                    Map.entry("movement", movement)
                            )
                    );
                }
            }
        }
        return result;
    }

    class ShoeModelRepo {
        Map<Integer, ShoeModel>  repo;
        ShoeModelRepo( List<ShoeModel> stock)
        {
            this.repo = stock.stream()
                    .collect(Collectors.toMap(ShoeModel::getModel_id, Function.identity()));
        }
        ShoeModel getModelById(int model_id){
            return this.repo.get(model_id);
        }

    }
}

package com.example.shop.core;

import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.AvailableShoe;
import com.example.shop.dto.out.Catalog;
import com.example.shop.dto.out.ShoeModel;
import com.example.shop.dto.out.Stock;
import com.example.demo.facade.ShoeFacade;
import com.example.shop.facade.ShopFacade;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ComponentScan({ "com.example.demo.facade", "com.example.demo.core", "com.example.shop.facade", "com.example.shop.core"})
@ContextConfiguration(classes = TestConfig.class)
class ShopCoreImplTest {

    @Autowired
    @Qualifier("testDatabaseAdapter")
    private DatabaseAdapter databaseAdapter;

    @Autowired
    private ShopFacade shopFacade;

    @Autowired
    private ShoeFacade shoeFacade;



    @BeforeEach
    public void initForEach() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void cleanUp() { reset(databaseAdapter); }


    @Test
    void givenVersion3_whenGetCatalog_thenReturnShopCatalog() {

        // Database MOCK
        List<ShoeModel> shoeModels  = new ArrayList<>();
        shoeModels.add(ShoeModel.builder().name("Shop shoe").color("BLUE").size(39).build());
        shoeModels.add(ShoeModel.builder().name("Shop shoe").color("BLACK").size(40).build());

        // Mock Database
        when(databaseAdapter.getCatalog(new ModelFilter(null, null, null))).thenReturn(shoeModels);

        // test catalog
        Catalog catalogResult = shopFacade.get(3).catalog(new ModelFilter(null, null, null));

        // check result
        Catalog expectedShes = Catalog.builder().shoes(shoeModels).build();
        Assert.assertEquals("Check get:/shoes/search ShopResult", expectedShes, catalogResult);
    }

    @Test
    void givenInitialStock_whenGetStock_thenReturnInitialStock() {

        // Database MOCK
        when(databaseAdapter.getStock()).thenReturn(this.initialStock.getShoes());

        // test get Stock
        Stock stock = shopFacade.get(3).getStock();

        // check result
        Assert.assertEquals("Check Empty Stock", Stock.State.SOME, stock.state);
        Assert.assertEquals("Check get:/shoes/search ShopResult", this.initialStock, stock);
    }

    @Test
    void givenEmptyStock_whenGetStock_thenReturnEmptyStock() {

        // Database MOCK
        when(databaseAdapter.getStock()).thenReturn((Collections.emptyList()));

        // test get Stock
        Stock stock = shopFacade.get(3).getStock();

        // check result
        Assert.assertEquals("Check Empty Stock", Stock.State.EMPTY, stock.state);
        Assert.assertEquals("Check Empty Stock", Collections.emptyList(), stock.shoes);
    }

    @Test
    void givenFullStock_whenGetStock_thenReturnInitialStock() {

        // Database MOCK
        Stock fullStock = Stock.builder().state(Stock.State.FULL).shoes(
                List.of(
                        AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(20).build(),
                        AvailableShoe.builder().name("Shop shoe").color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
                )
        ).build();
        // Mock Database
        when(databaseAdapter.getStock()).thenReturn(fullStock.getShoes());

        // test get Stock
        Stock stock = shopFacade.get(3).getStock();

        // check result
        Assert.assertEquals("Check Empty Stock", Stock.State.FULL, stock.state);
        Assert.assertEquals("Check Empty Stock", fullStock, stock);
    }

    @Test
    void givenEmptyStock_whenRequestSomeShoes_thenThrowsInsufficientStockException() {

        // check the exception case
        Exception exception = assertThrows(Exception.class, () -> removeShoesFromStock(this.emptyStock,
            StockMovement.builder().name("Shop shoe")
                    .color("Black")
                    .size(BigInteger.valueOf(40))
                    .quantity(-10)
                    .build()
    ));

        // check result
        Assert.assertEquals("Check exception", InsufficientStockException.class, exception.getClass());
        Assert.assertTrue(exception.getMessage().contains("Not enough stock :"));
    }


    @Test
    void givenInitialStock_whenRequestMoreThanAvailableBoxes_thenThrowsInsufficientStockException() {

        // check the exception case
        Exception exception = assertThrows(Exception.class, () -> removeShoesFromStock(this.initialStock,
               StockMovement.builder().name("Shop shoe")
                       .color("Black")
                       .size(BigInteger.valueOf(40))
                       .quantity(-11)
                       .build()
       ));

        // check result
        Assert.assertEquals("Check exception", InsufficientStockException.class, exception.getClass());
        Assert.assertTrue(exception.getMessage().contains("Not enough stock :"));
    }


    @Test
    void givenInitialStock_whenRequestQuantityIsAvailable_thenSuccess() throws Exception {

        // Remove 5 shoes boxes from initial stock
          removeShoesFromStock(
                this.initialStock,
                StockMovement.builder().name("Shop shoe")
                        .color("BLACK")
                        .size(BigInteger.valueOf(40))
                        .quantity(-5)
                        .build()
        );

    }

    @Test
    void givenInitialStock_whenRequestAllAvailableBoxes_thenSuccess() throws Exception {

        // Remove 10 shoes boxes from initial stock
          removeShoesFromStock(
                this.initialStock,
                StockMovement.builder().name("Shop shoe")
                        .color("BLACK")
                        .size(BigInteger.valueOf(40))
                        .quantity(-10)
                        .build()
        );

    }

    @Test
    void givenInitialStock_whenAdditionExceedCapacity_thenThrowsCapacityReached() {

        // Add 11 shoes boxes into a stock already full
        Exception exception = assertThrows(Exception.class, () -> AddShoes(
              this.initialStock,
              StockMovement.builder().name("Shop shoe")
                      .color("Black")
                      .size(BigInteger.valueOf(40))
                      .quantity(11)
                      .build()
      ));
        // check result
        Assert.assertEquals("Check exception", CapacityReachedException.class, exception.getClass());
        Assert.assertTrue(exception.getMessage().contains("The quantity reaches the capacity limit of the shop :"));
    }

    @Test
    void givenFullStock_whenAddSomeShoes_thenThrowsCapacityReached() {

        // Add one shoes box in fullStock
        // check the corresponding exception
        Exception exception = assertThrows(Exception.class, () -> AddShoes(
              this.fullStock,
              StockMovement.builder().name("Shop shoe")
                      .color("Black")
                      .size(BigInteger.valueOf(40))
                      .quantity(1)
                      .build()
      ));

        // check result
        Assert.assertEquals("Check exception", CapacityReachedException.class, exception.getClass());
        Assert.assertTrue(exception.getMessage().contains("The quantity reaches the capacity limit of the shop :"));
    }


    @Test
    void givenInitialStock_whenEnoughCapacity_thenSuccess() throws Exception {
        // Add 5 shoes boxes in initial stock
        AddShoes(this.initialStock,
                StockMovement.builder().name("Shop shoe")
                        .color("Black")
                        .size(BigInteger.valueOf(40))
                        .quantity(5)
                        .build());
    }


    @Test
    void givenInitialStock_whenAddFullCapacity_thenSuccess() throws Exception {
        // Add 10 shoes boxes in initial stock
        AddShoes(this.initialStock,
                StockMovement.builder().name("Shop shoe")
                        .color("Black")
                        .size(BigInteger.valueOf(40))
                        .quantity(10)
                        .build());
    }

    @Test
    void givenInitialStock_whenAddedShoesExceedCapacity_thenThrowsCapacityReached() {

        // Add 11 shoes in initial Stock
        // Check corresponding exception
        Exception exception = assertThrows(Exception.class, () -> AddShoes(
                this.initialStock,
                StockMovement.builder().name("Shop shoe")
                        .color("Black")
                        .size(BigInteger.valueOf(40))
                        .quantity(11)
                        .build()
        ));

        // check result
        Assert.assertEquals("Check exception", CapacityReachedException.class, exception.getClass());
        Assert.assertTrue(exception.getMessage().contains("The quantity reaches the capacity limit of the shop :"));
    }

    @Test
    void givenInitialStock_whenAddedAddMultiLineStockInCapacity_thenReturnSuccess() throws Exception {

        // Add 30 shoes Boxes and remove 20.
        int result  = this.updateStock(
                this.initialStock,
                StockMovement.builder().name("Shop shoe").color("Black").size(BigInteger.valueOf(40)).quantity(-20).build(),
                StockMovement.builder().name("Shop shoe").color("Black").size(BigInteger.valueOf(40)).quantity(20).build(),
                StockMovement.builder().name("Shop shoe").color("Black").size(BigInteger.valueOf(40)).quantity(10).build()
        );

        // check the number of shoes boxes movements
        Assert.assertEquals("Check exception", 10, result);

    }



    @Test
    void givenInitialStock_whenAddedAddMultiLineStockOutsideCapacity_thenThrowsCapacityReached() {

        // Add 31 shoes Boxes and remove 20.
        // check corresponding exception
        Exception exception = assertThrows(Exception.class, () -> this.updateStock(
              this.initialStock,
              StockMovement.builder().name("Shop shoe").color("Black").size(BigInteger.valueOf(40)).quantity(-20).build(),
              StockMovement.builder().name("Shop shoe").color("Black").size(BigInteger.valueOf(40)).quantity(20).build(),
              StockMovement.builder().name("Shop shoe").color("Black").size(BigInteger.valueOf(40)).quantity(11).build()
      ));

        // check result
        Assert.assertEquals("Check exception", CapacityReachedException.class, exception.getClass());
        Assert.assertTrue(exception.getMessage().contains("The quantity reaches the capacity limit of the shop :"));
    }

    /** Data set for test and Mock database state
     *
     */
    Stock initialStock = Stock.builder().state(Stock.State.SOME).shoes(
            List.of(
                    AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build(),
                    AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(41L)).quantity(0).build(),
                    AvailableShoe.builder().name("Shop shoe").color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
            )
    ).build();

    Stock fullStock = Stock.builder().state(Stock.State.SOME).shoes(
            List.of(
                    AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build(),
                    AvailableShoe.builder().name("Shop shoe").color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build(),
                    AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(41L)).quantity(10).build()
            )
    ).build();


    Stock emptyStock = Stock.builder().state(Stock.State.SOME).shoes(
            Collections.emptyList()
    ).build();

    /** Mock handler for removeShoes
     *
     * @param currentStock Stock to Mock
     * @param shoesRequest Stock Movement, quantity must be negative
     * @return The number of Shoes Boxes removed from stock
     * @throws Exception Not enaughShoes or database Errors
     */


    private int removeShoesFromStock(Stock currentStock, StockMovement shoesRequest) throws Exception{

        // Database MOCK
        when(databaseAdapter.countShoes(any())).thenReturn(
                currentStock.getShoes().stream()
                        .filter(x -> (x.getSize().intValue() == shoesRequest.getSize().intValue() &&
                                x.getColor().equals(shoesRequest.getColor())
                        ))
                        .mapToInt(AvailableShoe::getQuantity)
                        .sum()
        );
        when(databaseAdapter.destock(shoesRequest)).thenReturn(shoesRequest.getQuantity());

        // test get Stock
        int requestResult = shopFacade.get(3).stockUpdate(
                Lists.list( shoesRequest )
        );

        verify(this.databaseAdapter, times(1)).countShoes(any());
        if(shoesRequest.getQuantity() != 0) {
            verify(this.databaseAdapter, times(1)).destock(shoesRequest);
        }

        // check result
        Assert.assertEquals("Check exception", requestResult, shoesRequest.getQuantity());

        return requestResult;
    }

    /** Implement Mock for adding some shoes boxes to Stock
     *
     *
     * @param currentStock Database Stock to mock
     * @param shoesAdditions The stock movement quantity must be positive
     * @return the number of Shoes Boxes Add to Stock
     * @throws Exception Quantity exceeds the shop capacity, or Data base Error
     */
    private int AddShoes(Stock currentStock, StockMovement ...shoesAdditions) throws Exception {
        return this.updateStock(currentStock, shoesAdditions);
    }

    /** Mock the behavior of the database in cases of mutli-lines mouvement
     *
     * @param currentStock initial stock for emulation
     * @param movements to apply to stock
     * @return the number of shoe boxes moved
     * @throws Exception CapacityReachedException
     */
    private int updateStock(Stock currentStock, StockMovement ...movements) throws Exception {
        int expectedResult = 0;
        int addedMvtCount = 0;
        int removedMvtCount = 0;
        int initialCount = currentStock.getShoes().stream()
                .mapToInt(AvailableShoe::getQuantity)
                .sum();
        for(StockMovement movement: movements){
            // count the cumulative movement of boxes
            expectedResult += movement.getQuantity();
            if(movement.getQuantity() < 0){
                // simulate corresponding movement from stock
                removedMvtCount ++;
                when(this.databaseAdapter.destock(movement))
                        .thenReturn(movement.getQuantity());
            } else {
                // simulate corresponding movement  to stock
                addedMvtCount ++;
                when(this.databaseAdapter.stock(movement))
                        .thenReturn(movement.getQuantity());
            }
        }
        when(this.databaseAdapter.countShoes(any()))
                .thenReturn(initialCount + expectedResult);

        // performs all movements in single call
        int requestResult = this.shopFacade.get(3).stockUpdate(List.of(movements));

        verify(this.databaseAdapter, times(1)).countShoes(any());
        verify(this.databaseAdapter, times(addedMvtCount)).stock(any());
        verify(this.databaseAdapter, times(removedMvtCount)).destock(any());

        // Check the number of shoes added
        Assert.assertEquals("Check the number of boxes movement", requestResult, expectedResult);

        return requestResult;
    }

    /** Mock the behavior of Database in case on single line of adding shoes boxes
     *
     * @param currentStock  simulated initial stock
     * @param shoesAddition Shoes movement to add to stock
     * @return balance of the moved shoes boxes
     * @throws Exception CapacityReachedException
     */
    private int AddShoes(Stock currentStock, StockMovement shoesAddition) throws Exception {


        // Database MOCK
        when(this.databaseAdapter.countShoes(any())).thenReturn(
                currentStock.getShoes().stream()
                        .mapToInt(AvailableShoe::getQuantity)
                        .sum()
        );
        when(this.databaseAdapter.stock(shoesAddition)).thenReturn(shoesAddition.getQuantity());

        // test get Stock
        int requestResult = this.shopFacade.get(3).stockUpdate(
                List.of(shoesAddition)
        );

        verify(this.databaseAdapter, times(1)).countShoes(any());
        verify(this.databaseAdapter, times(1)).stock(shoesAddition);

        // Check the number of shoes added
        Assert.assertEquals("Check exception", requestResult, shoesAddition.getQuantity());

        return requestResult;
    }

    class ShoeModelRepo {
        Map<Integer, ShoeModel>  repo;
        ShoeModelRepo( ShoeModel[] stock)
        {
            this.repo = List.of(stock).stream()
                    .collect(Collectors.toMap(ShoeModel::getModel_id, Function.identity()));
        }
        ShoeModel getModelById(int model_id){
            return this.repo.get(model_id);
        }

    }
}
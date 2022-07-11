package com.example.shop.core;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;
import com.example.demo.dto.out.Shoes;
import com.example.demo.dto.out.Stock;
import com.example.demo.facade.ShoeFacade;
import com.example.demo.facade.ShopFacade;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ComponentScan({ "com.example.demo.facade", "com.example.demo.core", "com.example.shop.core"})
@ContextConfiguration(classes = TestConfig.class)
class ShopCoreImplTest {

    @Autowired
    @Qualifier("testDatabaseAdapter")
    private DatabaseAdapter databaseAdapter;

    @Autowired
    private ShopFacade shopFacade;

    @Autowired
    private ShoeFacade shoeFacade;

    Stock initialStock = Stock.builder().state(Stock.State.SOME).shoes(
            List.of(
                    AvailableShoe.builder().color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build(),
                    AvailableShoe.builder().color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
            )
    ).build();

    Stock fullStock = Stock.builder().state(Stock.State.SOME).shoes(
            List.of(
                    AvailableShoe.builder().color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build(),
                    AvailableShoe.builder().color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build(),
                    AvailableShoe.builder().color("BLACK").size(BigInteger.valueOf(41L)).quantity(10).build()
            )
    ).build();


    Stock emptyStock = Stock.builder().state(Stock.State.SOME).shoes(
            Collections.emptyList()
    ).build();


    @BeforeEach
    public void initForEach() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void cleanUp() { reset(databaseAdapter); }


    @Test
    void givenVersion3_whenSearch_thenReturnShopCatalog() {

        // Database MOCK
        List<Shoe> shopCatalog  = new ArrayList<Shoe>();
        shopCatalog.add(Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLUE).size(BigInteger.valueOf(39)).build());
        shopCatalog.add(Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLACK).size(BigInteger.valueOf(40)).build());

        when(databaseAdapter.getCatalog(new ShoeFilter(null, null))).thenReturn(shopCatalog);

        // test search
        Shoes catalogResult = shoeFacade.get(3).search(new ShoeFilter(null, null));

        // check result
        Shoes expectedShes = Shoes.builder().shoes(shopCatalog).build();
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
                        AvailableShoe.builder().color("BLACK").size(BigInteger.valueOf(40L)).quantity(20).build(),
                        AvailableShoe.builder().color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
                )
        ).build();
        when(databaseAdapter.getStock()).thenReturn(fullStock.getShoes());

        // test get Stock
        Stock stock = shopFacade.get(3).getStock();

        // check result
        Assert.assertEquals("Check Empty Stock", Stock.State.FULL, stock.state);
        Assert.assertEquals("Check Empty Stock", fullStock, stock);
    }


}
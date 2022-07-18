package com.example.demo.controller;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.out.Shoe;
import com.example.demo.dto.out.Shoes;
import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

    public enum Version{
        LEGACY("1"),
        NEW("2"),
        SHOP("3");

        public final String tag;

        Version(String label) {
            this.tag = label;
        }
    }

    private Stock initialStock = Stock.builder().state(Stock.State.SOME).shoes(
            List.of(
                    AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build(),
                    AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(41L)).quantity(0).build(),
                    AvailableShoe.builder().name("Shop shoe").color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
            )).build();

    private Catalog initialCatalog = Catalog.builder().shoes(
            List.of(
                    ShoeModel.builder().name("Shop shoe").color("BLACK").size(40).build(),
                    ShoeModel.builder().name("Shop shoe").color("BLACK").size(41).build(),
                    ShoeModel.builder().name("Shop shoe").color("BLUE").size(39).build()
            )
                        ).build();
    private RestTemplate restTemplate;

    private  Shoes newCoreDefaultShoes = Shoes.builder()
            .shoes(List.of(Shoe.builder()
                    .name("New shoe")
                    .color(ShoeFilter.Color.BLUE)
                    .size(BigInteger.ONE)
                    .build()))
            .build();

    @LocalServerPort
    int randomServerPort;

    String shoesUrl;
    String shopUrl;


    @Before
    public void init(){

        // use Apache Client Factory because the default RequestTemplate does not implement Patch method

        this.restTemplate = new RestTemplate();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);


        shoesUrl = "http://localhost:"+randomServerPort+"/shoes/";
        shopUrl = "http://localhost:"+randomServerPort+"/shop/";
    }



    @DisplayName("Test: legacy ")
    @Test
    public void givenLegacy_whenSearchWithoutFilter_thenSuccess()
    {

        Shoes shoes = this.search(Version.LEGACY);

        Shoes expectedShoes = Shoes.builder()
                .shoes(List.of(Shoe.builder()
                        .name("Legacy shoe")
                        .color(ShoeFilter.Color.BLUE)
                        .size(BigInteger.ONE)
                        .build()))
                .build();

        Assert.assertEquals("Legacy shoes",expectedShoes, shoes);
    }

    @DisplayName("Test: new ")
    @Test
    public void givenNew_whenSearchWithoutFilter_thenSuccess()
    {

        Shoes shoes = this.search(Version.NEW);

        Shoes expectedShoes = Shoes.builder()
                .shoes(List.of(Shoe.builder()
                        .name("New shoe")
                        .color(ShoeFilter.Color.BLACK)
                        .size(BigInteger.TWO)
                        .build()))
                .build();

        Assert.assertEquals("Legacy shoes",expectedShoes, shoes);
    }

    @DisplayName("Test: new ")
    @Test
    public void givenNew_whenSearchWithFilter_thenSuccess() {

        Shoes shoes = this.search(Version.NEW,
                new ShoeFilter( BigInteger.ONE, ShoeFilter.Color.BLUE));

        Shoes expectedShoes = Shoes.builder()
                .shoes(List.of(Shoe.builder()
                        .name("New shoe")
                        .color(ShoeFilter.Color.BLUE)
                        .size(BigInteger.ONE)
                        .build()))
                .build();

        Assert.assertEquals("Legacy shoes",expectedShoes, shoes);
    }

    @DisplayName("Test: shop")
    @Test
    public void givenNew_whenSearchWithSizeFilterOnly_thenSuccess()
    {

        Shoes shoes = this.search(Version.NEW,
                new ShoeFilter( BigInteger.valueOf(42L), null));


        Shoes expectedShoes = Shoes.builder()
                .shoes(List.of(Shoe.builder()
                        .name("New shoe")
                        .color(ShoeFilter.Color.BLACK)
                        .size(BigInteger.valueOf(42L))
                        .build())
                ).build();

        Assert.assertEquals("Legacy shoes",expectedShoes, shoes);
    }

    @Test
    public void givenNew_whenSearchWithColorFilterOnly_thenSuccess()
    {

        Shoes shoes = this.search(Version.NEW,
                new ShoeFilter( null, ShoeFilter.Color.BLUE));


        Shoes expectedShoes = Shoes.builder()
                .shoes(List.of(Shoe.builder()
                        .name("New shoe")
                        .color(ShoeFilter.Color.BLUE)
                        .size(BigInteger.TWO)
                        .build())
                ).build();

        Assert.assertEquals("Legacy shoes",expectedShoes, shoes);
    }


    @Test
    public void givenShoesShop_whenGetShoe_thenSuccess()
    {
        Catalog catalog = this.catalog();

        Catalog expectedShoes = this.initialCatalog;
/*                Shoes.builder().shoes(
                List.of(
                        Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLACK).size(BigInteger.valueOf(40L)).build(),
                        Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLACK).size(BigInteger.valueOf(41L)).build(),
                        Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLUE).size(BigInteger.valueOf(39L)).build()
                )
        ).build(); */
        Assert.assertEquals("check catalog", expectedShoes.getShoes().stream().sorted().collect(Collectors.toList()),
                catalog.getShoes().stream().sorted().collect(Collectors.toList()));
    }

    @Test
    public void givenShoesShop_whenGetCatalogWithFilter_thenSuccess()
    {
        Catalog catalog = this.catalog( ModelFilter.builder().size(39).color("BLUE").build());

        Catalog expectedShoes = Catalog.builder().shoes(
                List.of(
                        ShoeModel.builder().name("Shop shoe").color("BLUE").size(39).build()
                )
        ).build();
        Assert.assertEquals("Legacy shoes", expectedShoes.getShoes().stream().sorted().collect(Collectors.toList()),
                catalog.getShoes().stream().sorted().collect(Collectors.toList()));
    }

    @Test
    public void givenShoesShop_whenGetStock_thenSuccess()
    {
        Stock result = getStock();

        Stock expectedStock = this.initialStock;

        Assert.assertEquals("Legacy shoes",
                expectedStock.getShoes().stream().sorted().collect(Collectors.toList()),
                result.getShoes().stream().sorted().collect(Collectors.toList()));
    }

    @Test
    public void givenShoesShop_whenUpdateStockWithoutColor_thenThrowInvalidColor() throws JsonProcessingException {
        HttpClientErrorException exception = (HttpClientErrorException)assertThrows(Exception.class, () -> {
            int movementBalance =  updateSock(
                    StockMovement.builder().name("Shop shoe").size(BigInteger.valueOf(40L)).quantity(10).build()
            );
        });

        Assert.assertEquals("Check HTTP 400 Error", 400, exception.getRawStatusCode());
        ApiError error = new ObjectMapper().readValue(exception.getResponseBodyAsString(), ApiError.class);
        Assert.assertEquals("Check API ERROR", "color shouldn't be null", error.getSubErrors().get("patch.stockMovements[0].color").getMessage());
    }

    @Test
    public void givenInitialStock_whenAddedShoesExceedCapacity_thenThrowsCapacityReached() throws JsonProcessingException {
        HttpClientErrorException exception = (HttpClientErrorException)assertThrows(Exception.class, () -> {
            int movementBalance =  updateSock(
                    StockMovement.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(30).build()
            );
        });

        Assert.assertEquals("Check HTTP 400 Error", 400, exception.getRawStatusCode());
        ApiError error = new ObjectMapper().readValue(exception.getResponseBodyAsString(), ApiError.class);
        Assert.assertTrue("Check API ERROR"
                ,error.getSubErrors().get("stockMovement").getMessage().startsWith("The quantity reaches the capacity limit of the shop : ")
                );
    }

    @Test
    public void givenInitialStock_whenAddedAddMultiLineStockOutsideCapacity_thenThrowsCapacityReached() throws JsonProcessingException {
        HttpClientErrorException exception = (HttpClientErrorException)assertThrows(Exception.class, () -> {
            int movementBalance =  updateSock(
                    StockMovement.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(-30).build()
            );
        });

        Assert.assertEquals("Check HTTP 400 Error", 400, exception.getRawStatusCode());
        ApiError error = new ObjectMapper().readValue(exception.getResponseBodyAsString(), ApiError.class);
        Assert.assertTrue("Check API ERROR"
                , error.getSubErrors().get("stockMovement").getMessage().startsWith("Not enough stock :"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenShoesShop_whenUpdateStock_thenSuccess()
    {
        // Add 10 Boxes to stock
        Integer boxMovement = updateSock(
                StockMovement.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build()
        );
        Assert.assertEquals("Check BoxMovement", Integer.valueOf(10), boxMovement);

        // remove 5 boxes from stock
        boxMovement = updateSock(
                StockMovement.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(-15).build()
        );
        Assert.assertEquals("Check the number of stocked shoes", Integer.valueOf(-15), boxMovement);

        // Check resultant stock
        Stock stockResult = getStock();

        //Verify expected stock
        Stock expectedStock = Stock.builder().state(Stock.State.SOME).shoes(
                List.of(
                        AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(5).build(),
                        AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(41L)).quantity(0).build(),
                        AvailableShoe.builder().name("Shop shoe").color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
                )).build();

        Assert.assertEquals("Legacy shoes",
                expectedStock.getShoes().stream().sorted().collect(Collectors.toList()),
                stockResult.getShoes().stream().sorted().collect(Collectors.toList()));

        // Create a new model by inserting a new model in stock
        boxMovement = updateSock(
                StockMovement.builder().name("Shop shoe").color("GREEN").size(BigInteger.valueOf(45L)).quantity(1).build()
                );
        Assert.assertEquals("1: means new model is inserted", Integer.valueOf(1), boxMovement);

        expectedStock = Stock.builder().state(Stock.State.SOME).shoes(
                List.of(
                        AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(40L)).quantity(5).build(),
                        AvailableShoe.builder().name("Shop shoe").color("GREEN").size(BigInteger.valueOf(45L)).quantity(1).build(),
                        AvailableShoe.builder().name("Shop shoe").color("BLACK").size(BigInteger.valueOf(41L)).quantity(0).build(),
                        AvailableShoe.builder().name("Shop shoe").color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
                )).build();

        stockResult = getStock();
        Assert.assertEquals("Legacy shoes",
                expectedStock.getShoes().stream().sorted().collect(Collectors.toList()),
                stockResult.getShoes().stream().sorted().collect(Collectors.toList()));

        Catalog catalog = this.catalog();

        Catalog expectedCatalog =
                Catalog.builder().shoes(
                List.of(
                        ShoeModel.builder().name("Shop shoe").color("BLACK").size(40).build(),
                        ShoeModel.builder().name("Shop shoe").color("GREEN").size(45).build(),
                        ShoeModel.builder().name("Shop shoe").color("BLACK").size(41).build(),
                        ShoeModel.builder().name("Shop shoe").color("BLUE").size(39).build()
                )
        ).build();
        Assert.assertEquals("Legacy shoes", expectedCatalog.getShoes().stream().sorted().collect(Collectors.toList()),
                catalog.getShoes().stream().sorted().collect(Collectors.toList()));
    }



    private Catalog catalog(){
        return this.catalog(null);
    }

    private Catalog catalog(ModelFilter filter  ){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(this.shopUrl+"catalog");
        if(filter != null && filter.getSize()!= null ) {
            builder = builder.queryParam("size", filter.getSize());
        }
        if(filter != null && filter.getColor()!= null ) {

            builder = builder.queryParam("color", filter.getColor());
        }
        if(filter != null && filter.getName()!= null ) {
            builder = builder.queryParam("name", filter.getName());
        }
        String uriBuilder = builder.build().encode().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("version", Version.SHOP.tag);

        HttpEntity<Catalog> request = new HttpEntity<>(headers);

        // make an HTTP GET request with headers
        ResponseEntity<Catalog> result = restTemplate.exchange(
                uriBuilder,
                HttpMethod.GET,
                request,
                Catalog.class
        );

        //Verify http code
        Assert.assertEquals("Http code",200,result.getStatusCode().value());

        return result.getBody();
    }


    private Shoes search(Version version){
        return search(version , null);
    }

    private Shoes search(Version version, ShoeFilter filter  ){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(shoesUrl+"search");
        if(filter != null && filter.getSize().isPresent() ) {
            builder = builder.queryParam("size", filter.getSize().get());
        }
        if(filter != null && filter.getColor().isPresent() ) {
            builder = builder.queryParam("color", filter.getColor().get().name());
        }
        String uriBuilder = builder.build().encode().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("version", version.tag);

        HttpEntity<Shoes> request = new HttpEntity<>(headers);

        // make an HTTP GET request with headers
        ResponseEntity<Shoes> result = restTemplate.exchange(
                uriBuilder,
                HttpMethod.GET,
                request,
                Shoes.class
        );

        //Verify http code
        Assert.assertEquals("Http code",200,result.getStatusCode().value());

        return result.getBody();
    }

    Stock getStock(){
        return getStock(null);
    }

    Stock getStock(ShoeFilter filter){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(shopUrl+"stock");
        if(filter != null && filter.getSize().isPresent() ) {
            builder = builder.queryParam("size", filter.getSize().get());
        }
        if(filter != null && filter.getColor().isPresent() ) {
            builder = builder.queryParam("color", filter.getColor().get().name());
        }
        String uriBuilder = builder.build().encode().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("version", Version.SHOP.tag);

        HttpEntity<Stock> request = new HttpEntity<>(headers);

        // make an HTTP GET request with headers
        ResponseEntity<Stock> result = restTemplate.exchange(
                uriBuilder,
                HttpMethod.GET,
                request,
                Stock.class
        );

        //Verify http code
        Assert.assertEquals("Http code",200,result.getStatusCode().value());

        return result.getBody();
    }

    ResponseEntity<Integer> restUpdateSock(StockMovement ...stockMvts){
        HttpHeaders headers = new HttpHeaders();
        headers.set("version", Version.SHOP.tag);
        List<MediaType> acceptableMediaTypes = new java.util.ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptableMediaTypes);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        HttpEntity<StockMovement[]> request = new HttpEntity<>(stockMvts, headers);


        //       RestTemplate restTemplate = new RestTemplate();
        // make an HTTP GET request with headers

        return restTemplate.exchange(
                shopUrl+"stock",
                HttpMethod.PATCH,
                request,
                Integer.class
        );

    }

    Integer updateSock(StockMovement ...stockMvts){
        ResponseEntity<Integer> result = this.restUpdateSock( stockMvts);

        //Verify http result
        Assert.assertEquals("Http code",200,result.getStatusCode().value());

        return result.getBody();
    }

}

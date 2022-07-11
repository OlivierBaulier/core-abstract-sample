package com.example.demo.controller;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;
import com.example.demo.dto.out.Shoes;
import com.example.demo.dto.out.Stock;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;


@DisplayName("Test legacy vs new core")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

    public enum Version{
        LEGACY("1"),
        NEW("2"),
        SHOP("3")
        ;

        public final String tag;

        private Version(String label) {
            this.tag = label;
        }
    }

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

    String baseUrl;


    @Before
    public void init(){

        // use Apache Client Factory because the default RequestTemplate does not implement Patch method

        this.restTemplate = new RestTemplate();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);


        baseUrl = "http://localhost:"+randomServerPort+"/shoes/";
    }



    @DisplayName("Test: legacy on '/shoes/search'")
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

    @DisplayName("Test: get shoes on new core without filter")
    @Test
    public void givenNewShow_whenSearchWithoutFilter_thenSuccess()
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

    @DisplayName("Test: get shoes on new core withe filter ")
    @Test
    public void givenShoesShop_whenSearchWithFilter_thenSuccess() {

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

    @DisplayName("Test: get shoes on new core  with a Size filter only")
    @Test
    public void givenShoesShop_whenSearchWithSizeFilterOnly_thenSuccess()
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

    @DisplayName("Test: get shoes on new core  with a Size filter only")
    @Test
    public void givenShoesShop_whenSearchWithColorFilterOnly_thenSuccess()
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


    @DisplayName("Test: get shoes on shop core")
    @Test
    public void givenShoesShop_whenGetShoe_thenSuccess()
    {
        Shoes shoes = this.search(Version.SHOP);

        Shoes expectedShoes = Shoes.builder().shoes(
                List.of(
                        Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLACK).size(BigInteger.valueOf(40L)).build(),
                        Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLUE).size(BigInteger.valueOf(39L)).build()
                )
        ).build();
        Assert.assertEquals("Legacy shoes", expectedShoes.getShoes().stream().sorted().collect(Collectors.toList()),
                shoes.getShoes().stream().sorted().collect(Collectors.toList()));
    }

    @DisplayName("Test: get shoes on shop core")
    @Test
    public void givenShoesShop_whenGetShoeWithFilter_thenSuccess()
    {
        Shoes shoes = this.search(Version.SHOP, new ShoeFilter(BigInteger.valueOf(39L), ShoeFilter.Color.BLUE));

        Shoes expectedShoes = Shoes.builder().shoes(
                List.of(
                        Shoe.builder().name("Shop shoe").color(ShoeFilter.Color.BLUE).size(BigInteger.valueOf(39L)).build()
                )
        ).build();
        Assert.assertEquals("Legacy shoes", expectedShoes.getShoes().stream().sorted().collect(Collectors.toList()),
                shoes.getShoes().stream().sorted().collect(Collectors.toList()));
    }

    @DisplayName("Test: get the store stock")
    @Test
    public void givenShoesShop_whenGetStock_thenSuccess()
    {
        Stock result = getStock();

        //Verify expected stock
        Stock expectedStock = Stock.builder().state(Stock.State.SOME).shoes(
                List.of(
                        AvailableShoe.builder().color("BLACK").size(BigInteger.valueOf(40L)).quantity(10).build(),
                        AvailableShoe.builder().color("BLUE").size(BigInteger.valueOf(39L)).quantity(10).build()
                )
        ).build();
        Assert.assertEquals("Legacy shoes",
                expectedStock.getShoes().stream().sorted().collect(Collectors.toList()),
                result.getShoes().stream().sorted().collect(Collectors.toList()));
    }


    private Shoes search(Version version){
        return search(version , null);
    }

    private Shoes search(Version version, ShoeFilter filter  ){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl+"search");
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl+"stock");
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
}

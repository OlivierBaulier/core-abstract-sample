package com.example.shop.controller;

import com.example.shop.dto.in.RestStockMovement;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.ApiError;
import com.example.shop.dto.out.Stock;
import com.example.shop.facade.ShopFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Tag(name = "Shop Stock", description = "the Shoe Stock API of the Shop")
@RequestMapping(path = "/shop")
@RequiredArgsConstructor
public class ShopStockController {

    private final ShopFacade shopFacade;
    @Operation(summary = "Checks the availability of shoes stock, Error( Validation )")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @GetMapping(path = "/stock", produces = "application/json")
    public ResponseEntity<Stock> getStock(@RequestHeader(defaultValue = "3") Integer version) {
        return ResponseEntity.ok(shopFacade.get(version).getStock());
    }

    @Operation(summary = "Performs the movements in the shop stock." +
            "- A negative quantity for the stock inflows ." +
            "- A positive quantity for the stock outflows. Error( Validation, Full, InsufficientStock)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the balance of all movements in integer"),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = "{\n" +
                                                    "    \"status\": \"BAD_REQUEST\",\n" +
                                                    "    \"timestamp\": \"2022-07-16T17:50:03.1049042\",\n" +
                                                    "    \"subErrors\": {\n" +
                                                    "        \"patch.stockMovements[0].color\": {\n" +
                                                    "            \"code\": \"javax.validation.constraints.NotNull\",\n" +
                                                    "            \"message\": \"color shouldn't be null\",\n" +
                                                    "            \"context\": {\n" +
                                                    "                \"size\": 40,\n" +
                                                    "                \"color\": null,\n" +
                                                    "                \"quantity\": 10\n" +
                                                    "            }\n" +
                                                    "        }\n" +
                                                    "    }\n" +
                                                    "}")})),
            @ApiResponse(responseCode = "400",
                    description = "Shop capacity is reached",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Shop capacity is reached",
                                            value = "{\n" +
                                                    "    \"status\": \"BAD_REQUEST\",\n" +
                                                    "    \"timestamp\": \"2022-07-17T14:57:30.0900312\",\n" +
                                                    "    \"subErrors\": {\n" +
                                                    "        \"stockMovement\": {\n" +
                                                    "            \"code\": \"com.example.shop.core.CapacityReachedException\",\n" +
                                                    "            \"message\": \"The quantity reaches the capacity limit of the shop : (free places 10 : addition 40)\",\n" +
                                                    "            \"context\": {\n" +
                                                    "                \"freePlaces\": 10,\n" +
                                                    "                \"movement\": {\n" +
                                                    "                    \"size\": 40,\n" +
                                                    "                    \"color\": \"BLACK\",\n" +
                                                    "                    \"quantity\": 40\n" +
                                                    "                }\n" +
                                                    "            }\n" +
                                                    "        }\n" +
                                                    "    }\n" +
                                                    "}")}))
    })
    @PatchMapping(path = "/stock", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Integer> patch( @RequestHeader(defaultValue = "3") Integer version,
                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
                                                  @ExampleObject(
                                                          name = "Add 10 shoes boxes",
                                                          value = "{ \"name\": \"Shop shoe\", \"size\": 40, \"color\": \"BLACK\", \"quantity\": 10 }"),
                                                  @ExampleObject(
                                                          name = "remove 3 shoes box",
                                                          value = "{ \"name\": \"Shop shoe\", \"size\": 39, \"color\": \"BLUE\", \"quantity\": -3 }"),
                                                  @ExampleObject(
                                                          name = "multi-lines update",
                                                          value = "[{  \"name\": \"Shop shoe\", \"size\": 40, \"color\": \"BLACK\", \"quantity\": -2 }," +
                                                                  "{ \"name\": \"Shop shoe\", \"size\": 39, \"color\": \"BLUE\", \"quantity\": 10 }]")
                                          }))
                                          @RequestBody List<@Valid StockMovement> stockMovements) throws Exception {

        return ResponseEntity.ok(shopFacade.get(version).stockUpdate(stockMovements));

    }

    @Operation(summary = "Performs the movements in the shop stock using model reference to be conform to the REST resource notion.\n" +
            "A negative quantity for the stock inflows .\n" +
            "A positive quantity for the stock outflows.\n Error( Validation, Full, InsufficientStock)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the balance of all movements in integer"),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = "{\n" +
                                                    "    \"status\": \"BAD_REQUEST\",\n" +
                                                    "    \"timestamp\": \"2022-07-16T17:50:03.1049042\",\n" +
                                                    "    \"subErrors\": {\n" +
                                                    "        \"patch.stockMovements[0].color\": {\n" +
                                                    "            \"code\": \"javax.validation.constraints.NotNull\",\n" +
                                                    "            \"message\": \"color shouldn't be null\",\n" +
                                                    "            \"context\": {\n" +
                                                    "                \"size\": 40,\n" +
                                                    "                \"color\": null,\n" +
                                                    "                \"quantity\": 10\n" +
                                                    "            }\n" +
                                                    "        }\n" +
                                                    "    }\n" +
                                                    "}")})),
            @ApiResponse(responseCode = "400",
                    description = "Shop capacity is reached",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Shop capacity is reached",
                                            value = "{\n" +
                                                    "    \"status\": \"BAD_REQUEST\",\n" +
                                                    "    \"timestamp\": \"2022-07-17T14:57:30.0900312\",\n" +
                                                    "    \"subErrors\": {\n" +
                                                    "        \"stockMovement\": {\n" +
                                                    "            \"code\": \"com.example.shop.core.CapacityReachedException\",\n" +
                                                    "            \"message\": \"The quantity reaches the capacity limit of the shop : (free places 10 : addition 40)\",\n" +
                                                    "            \"context\": {\n" +
                                                    "                \"freePlaces\": 10,\n" +
                                                    "                \"movement\": {\n" +
                                                    "                    \"size\": 40,\n" +
                                                    "                    \"color\": \"BLACK\",\n" +
                                                    "                    \"quantity\": 40\n" +
                                                    "                }\n" +
                                                    "            }\n" +
                                                    "        }\n" +
                                                    "    }\n" +
                                                    "}")}))
    })
    @PatchMapping(path = "/rest/stock", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Integer> updateStock( @RequestHeader(defaultValue = "3") Integer version,
                                                @RequestBody List<@Valid RestStockMovement> stockMovements)  {
        return ResponseEntity.ok(shopFacade.get(version).restStockUpdate(stockMovements));
    }

}

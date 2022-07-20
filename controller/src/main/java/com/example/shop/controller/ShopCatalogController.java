package com.example.shop.controller;

import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.RestStockMovement;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.ApiError;
import com.example.shop.dto.out.Catalog;
import com.example.shop.dto.out.ShoeModel;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Tag(name = "Shop Catalog", description = "the Catalog API of the shop")
@RequestMapping(path = "/shop/catalog")
@RequiredArgsConstructor
public class ShopCatalogController {


    private final ShopFacade shopFacade;


    @Operation(summary = "get the shoe models in catalog, Error( Validation )")
    @GetMapping(produces = "application/json")
    public ResponseEntity<Catalog> getCatalog(ModelFilter filter, @RequestHeader(defaultValue = "3") Integer version) {

        try {
            return ResponseEntity.ok(shopFacade.get(version).catalog(filter));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(summary = "add a new model in catalog, this REST method is idempotent, if model exists it will be updated, Error( Validation )")
    @PutMapping( consumes = "application/json", produces = "application/json")
    public ResponseEntity<Integer> addOrUpdateShoeModel( @RequestHeader(defaultValue = "3") Integer version,
                                                         @RequestBody ShoeModel model)  {
        return ResponseEntity.ok(shopFacade.get(version).addOrUpdateShoeModel(model));
    }

    @Operation(summary = "get the shoe model by model_id in the catalog, Error( NotFound )")
    @GetMapping(path = "/{model_id}", produces = "application/json")
    public ResponseEntity<ShoeModel> getShoeModelById(@PathVariable("model_id") int model_id, @RequestHeader(defaultValue = "3") Integer version) {
            return ResponseEntity.ok(shopFacade.get(version).getShoeModelById(model_id));
    }



}


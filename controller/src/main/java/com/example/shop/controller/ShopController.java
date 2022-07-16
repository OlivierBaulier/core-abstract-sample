package com.example.shop.controller;

import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.Stock;
import com.example.shop.facade.ShopFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@Controller
@RequestMapping(path = "/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopFacade shopFacade;

    @GetMapping(path = "/stock")
    public ResponseEntity<Stock> get(@RequestHeader Integer version) {

        try {
            return ResponseEntity.ok(shopFacade.get(version).getStock());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @PatchMapping(path = "/stock", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Integer> patch( @RequestHeader Integer version, @RequestBody List<@Valid StockMovement> stockMovements) throws Exception {

        StockMovement[] stockMvmts = stockMovements.toArray(new StockMovement[stockMovements.size()]);
        return ResponseEntity.ok(shopFacade.get(version).stockUpdate(stockMvmts));

    }

}


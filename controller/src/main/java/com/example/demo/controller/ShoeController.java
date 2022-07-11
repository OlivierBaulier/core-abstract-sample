package com.example.demo.controller;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.Shoes;
import com.example.demo.dto.out.Stock;
import com.example.demo.facade.ShoeFacade;
import com.example.demo.facade.ShopFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/shoes")
@RequiredArgsConstructor
public class ShoeController {

  private final ShoeFacade shoeFacade;
  private final ShopFacade shopFacade;

  @GetMapping(path = "/search")
  public ResponseEntity<Shoes> all(ShoeFilter filter, @RequestHeader Integer version){

    return ResponseEntity.ok(shoeFacade.get(version).search(filter));

  }

  @GetMapping(path = "/stock")
  public ResponseEntity<Stock> get(@RequestHeader Integer version) {

    try {
      return ResponseEntity.ok(shopFacade.get(version).getStock());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  @PatchMapping(path = "/stock", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Integer> patch(@RequestBody StockMovement[] stockMvms, @RequestHeader Integer version) {

    try {
      return ResponseEntity.ok(shopFacade.get(version).stockUpdate(stockMvms));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

}

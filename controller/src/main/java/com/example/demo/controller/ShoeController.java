package com.example.demo.controller;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.Shoes;
import com.example.demo.dto.out.Stock;
import com.example.demo.facade.ShoeFacade;
import com.example.demo.facade.ShopFacade;
import com.example.shop.controller.ApiError;
import com.example.shop.controller.ApiSubError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.Arrays;

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
  public ResponseEntity<Integer> patch( @RequestBody @Valid StockMovement[] stockMvms, @RequestHeader Integer version) {
    try {
      return ResponseEntity.ok(shopFacade.get(version).stockUpdate(stockMvms));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  @PatchMapping(path = "/stack", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Integer> patch( @RequestBody @Valid StockMovement stockMvm, @RequestHeader Integer version) throws Exception {

      StockMovement[] stockMvmts = {stockMvm};
      return ResponseEntity.ok(shopFacade.get(version).stockUpdate(stockMvmts));

  }

/*
  @ExceptionHandler({ Exception.class })
  public void handleException() {
    //
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiError invalidArguments(MethodArgumentNotValidException exception){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST) ;
    exception.getBindingResult().getFieldErrors().forEach( error -> apiError.addSubError(
            error.getField(),
            ApiSubError.builder()
                    .code(error.getCode())
                    .message(error.getDefaultMessage())
                    .context(error.getRejectedValue()).build()
    ));
    return apiError;
  } */
}

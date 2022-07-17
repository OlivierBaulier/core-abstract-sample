package com.example.demo.controller;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.out.Shoes;
import com.example.demo.facade.ShoeFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Shoes", description = "the shoe catalog API")
@RequestMapping(path = "/shoes")
@RequiredArgsConstructor
public class ShoeController {

  private final ShoeFacade shoeFacade;

  @Operation(summary = "To consult the shoe models available in the catalogue\n" +
          "The filter allow the user to select shoe models wanted")
  @GetMapping(path = "/search", produces = "application/json")
  public ResponseEntity<Shoes> all(ShoeFilter filter, @RequestHeader(defaultValue = "3") Integer version){

    return ResponseEntity.ok(shoeFacade.get(version).search(filter));

  }


}

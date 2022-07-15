package com.example.shop.controller;

import com.example.shop.core.InsufficientStockException;
import com.example.shop.core.CapacityReachedException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@NoArgsConstructor
public class ApplicationExceptionHandler {

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
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CapacityReachedException.class)
    public ApiError capacityReached(CapacityReachedException exception){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST) ;
        apiError.addSubError(
                exception.getField(),
                ApiSubError.builder()
                        .code(CapacityReachedException.class.getName())
                        .message(exception.getMessage())
                        .context(exception.getCtx()).build()
        );
        return apiError;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientStockException.class)
    public ApiError insufficientStock(InsufficientStockException exception){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST) ;
        apiError.addSubError(
                exception.getField(),
                ApiSubError.builder()
                        .code(CapacityReachedException.class.getName())
                        .message(exception.getMessage())
                        .context(exception.getCtx()).build()
        );
        return apiError;
    }

}

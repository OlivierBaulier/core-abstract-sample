package com.example.shop.controller;

import com.example.demo.dto.out.Shoes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to manage common error from REST API or Business code
 */
@Data
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private Map<String, ApiSubError> subErrors;

    public ApiError(HttpStatus status, String name, ApiSubError subError){
        this(status);
        this.subErrors.put(name, subError);
    }

    public ApiError(HttpStatus status){
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.subErrors = new HashMap<>();
    }


    public void addSubError(String name, ApiSubError subError){
        this.subErrors.put(name, subError);
    }


    @JsonPOJOBuilder(withPrefix = "")
    public static class ApiErrorBuilder {

    }
}

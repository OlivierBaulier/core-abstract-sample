package com.example.shop.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to manage error in REST API
 * It allows the business errors to be returned to REST client
 */
@Data
public class ApiError {

    @Schema( example = "BAD_REQUEST")
    private HttpStatus status;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime timestamp;
    private Map<String, ApiSubError> subErrors;

    public ApiError(HttpStatus status, String name, ApiSubError subError){
        this(status);
        this.subErrors.put(name, subError);
    }

    public ApiError(HttpStatus status){
        this();
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(){
        this.subErrors = new HashMap<>();
    }

    public void addSubError(String name, ApiSubError subError){
        this.subErrors.put(name, subError);
    }


    @JsonPOJOBuilder(withPrefix = "")
    public static class ApiErrorBuilder {

    }
}

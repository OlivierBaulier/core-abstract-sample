package com.example.shop.core;


import lombok.Getter;

import java.io.Serializable;

public abstract  class ShopException extends RuntimeException  {

    @Getter private String field;
    @Getter private Object ctx;


    public ShopException(String field, String message, Object ctx) {
        super(message);
        this.ctx = ctx;
        this.field = field;
    }

}

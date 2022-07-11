package com.example.demo.core;

import com.example.demo.facade.ShoeFacade;
import com.example.demo.facade.ShopFacade;
import lombok.val;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Optional;

public abstract class AbstractShopCore implements ShopCore, ShoeCore {

    @Autowired
    private ShopFacade shopFacade;
    @Autowired
    private ShoeFacade shoeFacade;

    @PostConstruct
    void init(){

        val version = Optional.ofNullable(this.getClass().getAnnotation(Implementation.class))
                .map(Implementation::version)
                .orElseThrow(() -> new FatalBeanException("AbstractShoeCore implementation should be annotated with @Implementation"));

        shopFacade.register(version, this);
        shoeFacade.register(version, this);

    }

}

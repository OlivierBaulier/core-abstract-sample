
package com.example.shop.core;

import org.mockito.Mockito;
import org.springframework.context.annotation.*;

@Configuration
@Profile("test")
@ComponentScan({"com.example.demo.facade", "com.example.demo.core", "com.example.shop.facade", "com.example.shop.core"})
public class TestConfig {
    @Bean(name="testDatabaseAdapter")
    @Primary
    public DatabaseAdapter testDatabaseAdapter() {
        return Mockito.mock(DatabaseAdapter.class);
    }

}

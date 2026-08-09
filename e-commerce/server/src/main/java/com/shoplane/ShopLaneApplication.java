package com.shoplane;

import com.shoplane.config.ShopLaneProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ShopLaneProperties.class)
public class ShopLaneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopLaneApplication.class, args);
    }
}

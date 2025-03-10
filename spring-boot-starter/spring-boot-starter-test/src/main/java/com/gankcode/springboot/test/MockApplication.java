package com.gankcode.springboot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class
        }
)
public class MockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockApplication.class);
    }

}

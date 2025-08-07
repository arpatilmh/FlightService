package com.arpatilmh.flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlightApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightApplication.class, args);
        System.out.println("Hello, I am flight application");
    }
}


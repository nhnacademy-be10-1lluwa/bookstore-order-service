package com.nhnacademy.illuwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IlluwaApplication {

    public static void main(String[] args) {
        SpringApplication.run(IlluwaApplication.class, args);
    }

}

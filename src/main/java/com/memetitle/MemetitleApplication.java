package com.memetitle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MemetitleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemetitleApplication.class, args);
    }

}

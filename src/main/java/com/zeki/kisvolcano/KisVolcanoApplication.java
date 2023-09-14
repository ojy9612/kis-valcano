package com.zeki.kisvolcano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // createAt
public class KisVolcanoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KisVolcanoApplication.class, args);
    }

}

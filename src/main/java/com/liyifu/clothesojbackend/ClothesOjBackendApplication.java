package com.liyifu.clothesojbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.liyifu.clothesojbackend.mapper")
@SpringBootApplication
public class ClothesOjBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClothesOjBackendApplication.class, args);
    }

}

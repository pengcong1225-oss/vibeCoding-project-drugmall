package com.drugmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DrugMall应用启动类
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.drugmall.mapper")
public class DrugmallApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrugmallApplication.class, args);
        System.out.println("====================================");
        System.out.println("DrugMall Backend Started Successfully!");
        System.out.println("API Documentation: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
}

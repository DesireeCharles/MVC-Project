package com.Ise.ise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import com.Ise.ise.security.SecurityConfig;

@SpringBootApplication
@Import(SecurityConfig.class)
@ComponentScan("com.Ise")
public class IseApplication {

    public static void main(String[] args) {
        SpringApplication.run(IseApplication.class, args);
    }
}

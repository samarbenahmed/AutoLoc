package tn.esprit.spring.autolocsamarbenahmed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "tn.esprit.spring")
@EntityScan(basePackages = "tn.esprit.spring.entity")
@EnableJpaRepositories(basePackages = "tn.esprit.spring.repository")
public class AutoLocSamarBenahmedApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoLocSamarBenahmedApplication.class, args);
    }

}
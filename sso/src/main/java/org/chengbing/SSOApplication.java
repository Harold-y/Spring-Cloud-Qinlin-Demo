package org.chengbing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SSOApplication {
    public static void main(String[] args) {
        SpringApplication.run(SSOApplication.class, args);
    }
}

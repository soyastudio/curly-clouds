package soya.framework.curly.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"soya.framework.curly.application"})
public class CurlyCloudsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurlyCloudsApplication.class, args);
    }
}

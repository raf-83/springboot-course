package pl.rafal.springboot_course; // dostosuj do swojego package

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot! 👋";
    }

    @GetMapping("/info")
    public String info() {
        return "App name: " + appName;
    }
}

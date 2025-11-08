package aerospaceproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping
    public String helloWorld() {
        return "Hello World Sprint Boot";
    }

    @GetMapping("/api/test")
    public Map<String, String> testConnection() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend connection successful!");
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }

}

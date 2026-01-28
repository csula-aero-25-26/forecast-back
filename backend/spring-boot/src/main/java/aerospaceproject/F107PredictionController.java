package aerospaceproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/predictions")
public class F107PredictionController {

    private final F107PredictionService service;
    private final RestTemplate restTemplate;

//    @Value("${model.service.url}")
//    private String flaskUrl;

    public F107PredictionController(F107PredictionService service, RestTemplate restTemplate) {
        this.service = service;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public List<F107Prediction> getPredictions() {
        return service.getAllPredictions();
    }

    @GetMapping("/{id}")
    public Optional<F107Prediction> getPrediction(@PathVariable Long id) {
        return service.getPrediction(id);
    }

    @PostMapping("/create")
    public F107Prediction createPrediction(@RequestBody F107Prediction f107Prediction) {
        return service.savePrediction(f107Prediction);
    }

//    // === ML Model Connection ===
//
//    // Endpoint acts as the bridge between spring boot and the python model service
//    // Forwards prediction request to Flask, then returns the result
//    @PostMapping("/run")
//    public String runModelPrediction(@RequestBody Map<String, Object> body) {
//
//        RestTemplate restTemplate = new RestTemplate(); // template for making HTTP requests
//        // Creates http headers that tell flask that the request is in JSON format.
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Wraps the JSON body + headers in a single request object
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//        // Sends the request to flask via the url (refer to application properties)
//        // Stores the response
//        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);
//
//        return response.getBody(); // returns the Flask prediction JSON
//    }


}

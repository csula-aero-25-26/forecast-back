package aerospaceproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/input")
public class F107InputDataController {

    private final F107InputDataService inputService;
    private final F107InputDataRepository inputRepo;
    private final F107PredictionRepository predictionRepo;
    private final RestTemplate restTemplate;

    @Value("${model.service.url}")
    private String flaskUrl;

    public F107InputDataController(F107InputDataService inputService,
                                   F107InputDataRepository inputRepo,
                                   F107PredictionRepository predictionRepo,
                                   RestTemplate restTemplate) {
        this.inputService = inputService;
        this.inputRepo = inputRepo;
        this.predictionRepo = predictionRepo;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public List<F107InputData> getInputs() {
        return inputService.getAllInputs();
    }

    @GetMapping("/{id}")
    public Optional<F107InputData> getInput(@PathVariable Long id) {
        return inputService.getInput(id);
    }


    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody F107InputData input) {
        try {
            // Save input data
            F107InputData savedInput = inputRepo.save(input);

            Map<String, Object> payload = new HashMap<>();
            Map<String, Object> features = new HashMap<>();

            if (input.getLags() != null) {
                for (int i = 0; i < input.getLags().size(); i++) {
                    features.put("lag" + (i + 1), input.getLags().get(i));
                }
            }

            features.put("ap_mean", input.getAp_mean());
            features.put("ap_max", input.getAp_max());
            features.put("ap_mean_lag1", input.getAp_mean_lag1());
            features.put("ap_mean_lag2", input.getAp_mean_lag2());
            features.put("ap_mean_lag3", input.getAp_mean_lag3());
            features.put("ap_max_lag1", input.getAp_max_lag1());
            features.put("ap_max_lag2", input.getAp_max_lag2());
            features.put("ap_max_lag3", input.getAp_max_lag3());

            payload.put("features", features);

            // Send the request to Flask model
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Extract and save the prediction
                Double predictedValue = (Double) response.getBody().get("prediction");
                F107Prediction prediction = new F107Prediction();
                prediction.setInput(savedInput);
                prediction.setDate(LocalDate.now());
                prediction.setPredictedValue(predictedValue);
                prediction.setModelVersion("v1.0");

                F107Prediction savedPrediction = predictionRepo.save(prediction);

                Map<String, Object> result = new HashMap<>();
                result.put("inputId", savedInput.getId());
                result.put("predictionId", savedPrediction.getId());
                result.put("predictedValue", predictedValue);

                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Invalid Response from Flask Model");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

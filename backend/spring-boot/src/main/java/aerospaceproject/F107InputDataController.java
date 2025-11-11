package aerospaceproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/input")
public class F107InputDataController {

    private final F107InputDataService inputService;
    private final F107InputDataRepository inputRepo;
    private final F107PredictionRepository predictionRepo;
    private final RestTemplate restTemplate;

    @Value("${model.service.url}")
    private String modelUrl;

    @Value("${fetch.service.url}")
    private String fetchUrl;

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

    // Gets the latest features from the fetch-service & sends to model-service
    @GetMapping("/predict-latest")
    public ResponseEntity<?> predictFromLatest() {
        try {
            // Fetch latest features
            ResponseEntity<Map> fetchResponse = restTemplate.getForEntity(fetchUrl, Map.class);
            if (!fetchResponse.getStatusCode().is2xxSuccessful() ||
                    fetchResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to fetch latest features from fetch-service.");
            }

            Map<String, Object> fetchBody = fetchResponse.getBody();
            Map<String, Object> features = (Map<String, Object>) fetchBody.get("features");
            if (features == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Fetch-service returned no 'features' object.");
            }

            List<Double> lags = new ArrayList<>();
            for (int i = 1; i <= 27; i++) {
                String key = "f107_lag_" + i;
                Object val = features.get(key);
                if (val == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing feature from fetch-service: " + key);
                }
                double d = ((Number) val).doubleValue();
                lags.add(d);
            }

            // Create and save input to DB
            F107InputData newInput = new F107InputData();

            newInput.setLags(lags);

            newInput.setAp_mean(((Number) features.get("ap_mean")).doubleValue());
            newInput.setAp_max(((Number) features.get("ap_max")).doubleValue());

            newInput.setAp_mean_lag1(((Number) features.get("ap_mean_lag1")).doubleValue());
            newInput.setAp_mean_lag2(((Number) features.get("ap_mean_lag2")).doubleValue());
            newInput.setAp_mean_lag3(((Number) features.get("ap_mean_lag3")).doubleValue());

            newInput.setAp_max_lag1(((Number) features.get("ap_max_lag1")).doubleValue());
            newInput.setAp_max_lag2(((Number) features.get("ap_max_lag2")).doubleValue());
            newInput.setAp_max_lag3(((Number) features.get("ap_max_lag3")).doubleValue());

            F107InputData savedInput = inputRepo.save(newInput);

            String modelId = "lgb_f107_lag27_ap_lag3";
            int horizonDays = 1;
            String predictUrl = buildPredictUrl(modelUrl, modelId, horizonDays);

            // Send features to model-service
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("features", features);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> modelResponse = restTemplate.postForEntity(predictUrl, request, Map.class);

            if (!modelResponse.getStatusCode().is2xxSuccessful() || modelResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to get prediction from model-service.");
            }

            Map<String, Object> body = modelResponse.getBody();
            Double predictedValue = ((Number) body.get("predicted_flux")).doubleValue();
            String returnedModelId = (String) body.get("model_id");

            // Create and save prediction
            F107Prediction prediction = new F107Prediction();
            prediction.setInput(savedInput);
            prediction.setDate(LocalDate.now());
            prediction.setPredictedValue(predictedValue);
            prediction.setModelVersion(returnedModelId != null ? returnedModelId : modelId);

            F107Prediction savedPrediction = predictionRepo.save(prediction);

            // Return result
            Map<String, Object> result = new HashMap<>();
            result.put("inputId", savedInput.getId());
            result.put("predictionId", savedPrediction.getId());
            result.put("predictedValue", predictedValue);
            result.put("modelVersion", modelId);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Helper method to build the model-service predict URL
    private String buildPredictUrl(String modelUrl, String modelId, int horizonDays) {
        String base = modelUrl;
        if (base.endsWith("/"))
            base = base.substring(0, base.length()-1);
        if (base.endsWith("predict"))
            base = base.substring(0, base.length() - "/predict".length());
        return String.format("%s/predict/%s/%d", base, modelId, horizonDays);
    }

    // Sends entered input to model-service and returns the prediction/saves it to db
    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody F107InputData input) {
        try {
            // Save input data locally
            F107InputData savedInput = inputRepo.save(input);

            // Create payload for FastAPI model-service
            Map<String, Object> payload = new HashMap<>();
            Map<String, Object> features = new HashMap<>();

            List<Double> lags = input.getLags();
            if (lags != null) {
                for (int i = 0; i < lags.size(); i++) {
                    features.put("f107_lag_" + (i + 1), lags.get(i));
                }
            }

            features.put("lags", input.getLags());
            features.put("ap_mean", input.getAp_mean());
            features.put("ap_max", input.getAp_max());
            features.put("ap_mean_lag1", input.getAp_mean_lag1());
            features.put("ap_mean_lag2", input.getAp_mean_lag2());
            features.put("ap_mean_lag3", input.getAp_mean_lag3());
            features.put("ap_max_lag1", input.getAp_max_lag1());
            features.put("ap_max_lag2", input.getAp_max_lag2());
            features.put("ap_max_lag3", input.getAp_max_lag3());

            payload.put("features", features);

            // FastAPI Endpoint
            String modelId = "lgb_f107_lag27_ap_lag3";
            int horizonDays = 1;
            String predictUrl = String.format("%s/predict/%s/%d", modelUrl, modelId, horizonDays);

            // Send the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Obtain the model response
            ResponseEntity<Map> response = restTemplate.postForEntity(predictUrl, request, Map.class);

            // Handle the response (if applicable) or indicate error
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Extract and save the prediction
                Double predictedValue = ((Number) response.getBody().get("predicted_flux")).doubleValue();

                // Save prediction & add to repo
                F107Prediction prediction = new F107Prediction();
                prediction.setInput(savedInput);
                prediction.setDate(LocalDate.now());
                prediction.setPredictedValue(predictedValue);
                prediction.setModelVersion((String) response.getBody().get("model_id"));

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

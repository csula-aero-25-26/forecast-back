package aerospaceproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/input")
public class F107InputDataController {

    private final F107InputDataService inputService;
    private final F107InputDataRepository inputRepo;
    private final F107PredictionRepository predictionRepo;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(F107InputDataController.class);

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
        logger.info("Received request: /api/input/predict-latest");

        try {
            logger.info("Calling fetch-service at {}", fetchUrl);

            // Fetch latest features
            String modelId = "lgb_f107_lag27_ap_lag3_horizon_1";
            String fetchEndpoint = String.format("%s/latest/%s", fetchUrl, modelId);

            ResponseEntity<Map> fetchResponse =
                    restTemplate.getForEntity(fetchEndpoint, Map.class);

            if (!fetchResponse.getStatusCode().is2xxSuccessful() ||
                    fetchResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to fetch latest features from fetch-service.");
            }

            Map<String, Object> fetchBody = fetchResponse.getBody();

            logger.info("Successfully retrieved features from fetch-service");

            Map<String, Object> features = (Map<String, Object>) fetchBody.get("features");
            if (features == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Fetch-service returned no 'features' object.");
            }
            // Create and save input to DB
            F107InputData newInput = new F107InputData();

            // Manually create a list of lags from returned features
            List<Double> lags = new ArrayList<>();

            if (features.containsKey("f107_lag_1")) {
                for (int i = 1; i <= 27; i++) {
                    String key = "f107_lag_" + i;
                    Object val = features.get(key);
                    if (val != null) {
                        lags.add(((Number) val).doubleValue());
                    }
                }
                newInput.setLags(lags);
            }






            if (features.containsKey("ap_mean"))
                newInput.setAp_mean(((Number) features.get("ap_mean")).doubleValue());

            if (features.containsKey("ap_max"))
                newInput.setAp_max(((Number) features.get("ap_max")).doubleValue());

            if (features.containsKey("ap_mean_lag1"))
                newInput.setAp_mean_lag1(((Number) features.get("ap_mean_lag1")).doubleValue());

            if (features.containsKey("ap_mean_lag2"))
                newInput.setAp_mean_lag2(((Number) features.get("ap_mean_lag2")).doubleValue());

            if (features.containsKey("ap_mean_lag3"))
                newInput.setAp_mean_lag3(((Number) features.get("ap_mean_lag3")).doubleValue());

            if (features.containsKey("ap_max_lag1"))
                newInput.setAp_max_lag1(((Number) features.get("ap_max_lag1")).doubleValue());

            if (features.containsKey("ap_max_lag2"))
                newInput.setAp_max_lag2(((Number) features.get("ap_max_lag2")).doubleValue());

            if (features.containsKey("ap_max_lag3"))
                newInput.setAp_max_lag3(((Number) features.get("ap_max_lag3")).doubleValue());


            F107InputData savedInput = inputRepo.save(newInput);

            String predictUrl = buildPredictUrl(modelUrl, modelId);


            logger.info("Calling model-service at {}", predictUrl);

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

            logger.info("Obtained prediction from model-service.");

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

            logger.info("F107 Prediction Obtained and Stored");

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

    @GetMapping("/predict-latest-v2")
    public ResponseEntity<?> predictFromLatestV2(
            @RequestParam(defaultValue = "lgb_f107_lag27_ap_lag3_horizon_1") String modelId
    )
    {

        logger.info("Prediction requested with model={}", modelId);

        try {
            logger.info("Calling fetch-service at {}", fetchUrl);

            // Fetch latest features
            String fetchEndpoint = String.format("%s/latest/%s", fetchUrl, modelId);
            ResponseEntity<Map> fetchResponse =
                    restTemplate.getForEntity(fetchEndpoint, Map.class);

            if (!fetchResponse.getStatusCode().is2xxSuccessful() ||
                    fetchResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to fetch latest features from fetch-service.");
            }

            Map<String, Object> fetchBody = fetchResponse.getBody();

            logger.info("Successfully retrieved features from fetch-service");

            Map<String, Object> features = (Map<String, Object>) fetchBody.get("features");
            if (features == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Fetch-service returned no 'features' object.");
            }


            // Create and save input to DB
            F107InputData newInput = new F107InputData();

            // Manually create a list of lags from returned features
            List<Double> lags = new ArrayList<>();

            if (features.containsKey("f107_lag_1")) {
                for (int i = 1; i <= 27; i++) {
                    String key = "f107_lag_" + i;
                    Object val = features.get(key);
                    if (val != null) {
                        lags.add(((Number) val).doubleValue());
                    }
                }
                newInput.setLags(lags);
            }


            if (features.containsKey("ap_mean"))
                newInput.setAp_mean(((Number) features.get("ap_mean")).doubleValue());

            if (features.containsKey("ap_max"))
                newInput.setAp_max(((Number) features.get("ap_max")).doubleValue());

            if (features.containsKey("ap_mean_lag1"))
                newInput.setAp_mean_lag1(((Number) features.get("ap_mean_lag1")).doubleValue());

            if (features.containsKey("ap_mean_lag2"))
                newInput.setAp_mean_lag2(((Number) features.get("ap_mean_lag2")).doubleValue());

            if (features.containsKey("ap_mean_lag3"))
                newInput.setAp_mean_lag3(((Number) features.get("ap_mean_lag3")).doubleValue());

            if (features.containsKey("ap_max_lag1"))
                newInput.setAp_max_lag1(((Number) features.get("ap_max_lag1")).doubleValue());

            if (features.containsKey("ap_max_lag2"))
                newInput.setAp_max_lag2(((Number) features.get("ap_max_lag2")).doubleValue());

            if (features.containsKey("ap_max_lag3"))
                newInput.setAp_max_lag3(((Number) features.get("ap_max_lag3")).doubleValue());


            F107InputData savedInput = inputRepo.save(newInput);

            String predictUrl = buildPredictUrl(modelUrl, modelId);


            logger.info("Calling model-service at {}", predictUrl);

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

            logger.info("Obtained prediction from model-service.");

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

            logger.info("F107 Prediction Obtained and Stored");

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
    private String buildPredictUrl(String modelUrl, String modelId) {
        String base = modelUrl;
        if (base.endsWith("/"))
            base = base.substring(0, base.length() - 1);
        return String.format("%s/predict/%s", base, modelId);
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
            String modelId = "lgb_f107_lag27_ap_lag3_horizon_1";


            String predictUrl = String.format("%s/predict/%s", modelUrl, modelId);


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

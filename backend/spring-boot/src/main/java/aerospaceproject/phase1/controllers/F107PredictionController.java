package aerospaceproject.phase1.controllers;

import aerospaceproject.phase1.entities.F107InputData;
import aerospaceproject.phase1.repositories.F107InputDataRepository;
import aerospaceproject.phase1.repositories.F107PredictionRepository;
import aerospaceproject.phase1.services.F107InputDataService;
import aerospaceproject.phase1.services.F107PredictionService;
import aerospaceproject.phase1.entities.F107Prediction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/predictions")
public class F107PredictionController {

    private final F107PredictionService service;
    private final RestTemplate restTemplate;

    private final F107InputDataService inputService;
    private final F107InputDataRepository inputRepo;
    private final F107PredictionRepository predictionRepo;

    private static final Logger logger = LoggerFactory.getLogger(F107InputDataController.class);

    @Value("${model.service.url}")
    private String modelUrl;

    @Value("${fetch.service.url}")
    private String fetchUrl;

    public F107PredictionController(F107PredictionService service,
                                    RestTemplate restTemplate,
                                    F107InputDataService inputService,
                                    F107InputDataRepository inputRepo,
                                    F107PredictionRepository predictionRepo) {
        this.service = service;
        this.restTemplate = restTemplate;
        this.inputService = inputService;
        this.inputRepo = inputRepo;
        this.predictionRepo = predictionRepo;
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

    @GetMapping("/latest")
    public ResponseEntity<?> predictFromLatestV2(
            @RequestParam(defaultValue = "lgb_f107_lag27_ap_lag3_horizon_1") String modelId
    ) {

        logger.info("PredictionV2 requested with model={}", modelId);

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

package aerospaceproject.phase2.controllers;

import aerospaceproject.phase2.dto.ManualOverrideRequest;
import aerospaceproject.phase2.entities.ModelRegistry;
import aerospaceproject.phase2.entities.Predictions;
import aerospaceproject.phase2.repositories.ModelRegistryRepository;
import aerospaceproject.phase2.services.PredictionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


// Runs the Prediction Workflow
@RestController
@RequestMapping("/api/inference")
public class InferenceController {

    private final ModelRegistryRepository modelRegistryRepository;
    private final PredictionsService predictionsService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(InferenceController.class);

    @Value("${model.service.url}")
    private String modelUrl;

    @Value("${fetch.service.url}")
    private String fetchUrl;

    public InferenceController(ModelRegistryRepository modelRegistryRepository,
                               PredictionsService predictionsService,
                               ObjectMapper objectMapper,
                               RestTemplate restTemplate) {
        this.modelRegistryRepository = modelRegistryRepository;
        this.predictionsService = predictionsService;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    // Refactored Version of predictFromLatestV2 (uses new phase2 classes)
    @GetMapping("/predict-latest-v2-phase2")
    public ResponseEntity<?> predictFromLatestV2Phase2(
            @RequestParam(defaultValue = "lgb_f107_lag27_ap_lag3_horizon_1") String modelId
    ) {

        logger.info("Phase2 Prediction requested with model={}", modelId);

        try {

            // Validate that modelID exists in ModelRegistry
            ModelRegistry model = modelRegistryRepository.findById(modelId)
                    .orElseThrow(() -> new RuntimeException("Model not found: " + modelId));

            // Fetch latest features
            String fetchEndpoint = String.format("%s/latest/%s", fetchUrl, modelId);
            logger.info("Calling fetch-service at {}", fetchEndpoint);

            ResponseEntity<Map> fetchResponse = restTemplate.getForEntity(fetchEndpoint, Map.class);

            if (!fetchResponse.getStatusCode().is2xxSuccessful()
             || fetchResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to fetch latest features");
            }

            Map<String, Object> fetchBody = fetchResponse.getBody();
            Map<String, Object> features =
                    (Map<String, Object>) fetchBody.get("features");

            if (features == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Fetch-service returned no features");
            }

            logger.info("Successfully retrieved features");

            // Call Model Service
            String predictUrl = buildPredictUrl(modelUrl, modelId);
            logger.info("Calling model-service at {}", predictUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("features", features);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(payload, headers);

            ResponseEntity<Map> modelResponse =
                    restTemplate.postForEntity(predictUrl, request, Map.class);

            if (!modelResponse.getStatusCode().is2xxSuccessful()
                || modelResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Model-service failed");
            }

            Map<String, Object> body = modelResponse.getBody();
            Double predictedValue =
                    ((Number) body.get("predicted_flux")).doubleValue();

            logger.info("Prediction received: {}", predictedValue);

            // Determine horizon from modelId
            Integer horizonDays = extractHorizonFromModelId(modelId);

            Optional<Predictions> existing =
                    predictionsService.findByModelAndTargetDate(model, LocalDate.now().plusDays(horizonDays));

            Predictions prediction;

            // Verify if the prediction exists (avoids duplicate entry errors)
            if (existing.isPresent()) {
                logger.info("Prediction already exists for model={} and target date={}",
                        model, LocalDate.now().plusDays(horizonDays));

                prediction = existing.get();
            } else {
                prediction = predictionsService.savePrediction(
                        model,
                        LocalDate.now(),
                        LocalDate.now().plusDays(horizonDays),
                        horizonDays,
                        predictedValue,
                        features,
                        "Generated via phase2 Endpoint"
                );
                logger.info("Phase2 prediction stored with ID={}",
                        prediction.getId());
            }

            // Return result
            Map<String, Object> result = new HashMap<>();
            result.put("predictionId", prediction.getId());
            result.put("predictedValue", predictedValue);
            result.put("modelId", modelId);
            result.put("horizonDays", horizonDays);
            result.put("features", features);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Phase2 predict failed", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Obtains all features from fetch-service used by a given model
    @GetMapping("/get-features")
    public ResponseEntity<?> getFeatures(
            @RequestParam(defaultValue = "lgb_f107_lag27_ap_lag3_horizon_1") String modelId
    ) {
        logger.info("Features requested with model={}", modelId);

        try {

            // Validate that modelID exists in ModelRegistry
            ModelRegistry model = modelRegistryRepository.findById(modelId)
                    .orElseThrow(() -> new RuntimeException("Model not found: " + modelId));

            // Fetch latest features
            String fetchEndpoint = String.format("%s/latest/%s", fetchUrl, modelId);
            logger.info("Calling fetch-service at {}", fetchEndpoint);

            ResponseEntity<Map> fetchResponse = restTemplate.getForEntity(fetchEndpoint, Map.class);

            if (!fetchResponse.getStatusCode().is2xxSuccessful()
                    || fetchResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to fetch latest features");
            }

            Map<String, Object> fetchBody = fetchResponse.getBody();
            Map<String, Object> features =
                    (Map<String, Object>) fetchBody.get("features");

            if (features == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Fetch-service returned no features");
            }

            logger.info("Successfully retrieved features");

            // Return result
            Map<String, Object> result = new HashMap<>();
            result.put("features", features);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("get-features failed", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("manual-override")
    public ResponseEntity<?> manualOverride(
            @RequestBody ManualOverrideRequest request)
    {
        try {
            String modelId = request.getModelId();
            Map<String, Object> features = request.getFeatures();

            logger.info("Manual override requested for model={}", modelId);

            // Validate model
            ModelRegistry model = modelRegistryRepository.findById(modelId)
                    .orElseThrow(() ->
                            new RuntimeException("Model not found: " + modelId));

            // Build model-service URL
            String predictUrl = buildPredictUrl(modelUrl, modelId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("features", features);

            HttpEntity<Map<String, Object>> requestEntity =
                    new HttpEntity<>(payload, headers);

            ResponseEntity<Map> modelResponse =
                    restTemplate.postForEntity(predictUrl, requestEntity, Map.class);

            if (!modelResponse.getStatusCode().is2xxSuccessful()
                || modelResponse.getBody() == null) {

                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Model-service failed");
            }

            Map<String, Object> body = modelResponse.getBody();
            Double predictedValue =
                    ((Number) body.get("predicted_flux")).doubleValue();

            Map<String, Object> result = new HashMap<>();
            result.put("modelId", modelId);
            result.put("predictedValue", predictedValue);
            result.put("manualOverride", true);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Manual override failed", e);
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

    // Helper method to extract the horizon days from modelId
    private Integer extractHorizonFromModelId(String modelId) {
        if (modelId.contains("horizon_")) {
            String[] parts = modelId.split("horizon_");
            return Integer.parseInt(parts[1]);
        }

        return 1; // Default
    }
}

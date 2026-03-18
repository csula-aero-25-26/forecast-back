package aerospaceproject.phase2.controllers;

import aerospaceproject.phase2.dto.ManualOverrideRequest;
import aerospaceproject.phase2.entities.FeaturesDaily;
import aerospaceproject.phase2.entities.ModelRegistry;
import aerospaceproject.phase2.entities.Prediction;
import aerospaceproject.phase2.repositories.ModelRegistryRepository;
import aerospaceproject.phase2.services.PredictionService;
import aerospaceproject.phase2.services.FeaturesDailyService;
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
    private final PredictionService predictionService;
    private final FeaturesDailyService featuresDailyService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(InferenceController.class);

    @Value("${model.service.url}")
    private String modelUrl;

    @Value("${fetch.service.url}")
    private String fetchUrl;

    public InferenceController(ModelRegistryRepository modelRegistryRepository,
                               PredictionService predictionService,
                               FeaturesDailyService featuresDailyService,
                               ObjectMapper objectMapper,
                               RestTemplate restTemplate) {
        this.modelRegistryRepository = modelRegistryRepository;
        this.predictionService = predictionService;
        this.featuresDailyService = featuresDailyService;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    private Map<String, Object> fetchAndCacheFeatures() {

        LocalDate today = LocalDate.now();

        // 1. Check DB first
        Optional<FeaturesDaily> existing =
                featuresDailyService.findByDate(today);

        if (existing.isPresent()) {
            logger.info("Using cached features for date={}", today);
            return featuresDailyService.toFeatureMap(existing.get());
        }

        // 2. Not cached → call fetch-service
        String fetchEndpoint = String.format("%s/latest", fetchUrl);
        logger.info("Calling fetch-service at {}", fetchEndpoint);

        ResponseEntity<Map> fetchResponse =
                restTemplate.getForEntity(fetchEndpoint, Map.class);

        if (!fetchResponse.getStatusCode().is2xxSuccessful()
                || fetchResponse.getBody() == null) {
            throw new RuntimeException("Failed to fetch latest features");
        }

        Map<String, Object> fetchBody = fetchResponse.getBody();
        Map<String, Object> features =
                (Map<String, Object>) fetchBody.get("features");

        if (features == null) {
            throw new RuntimeException("Fetch-service returned no features");
        }

        // 3. Save to DB
        featuresDailyService.save(today, features);
        logger.info("Cached features for date={}", today);

        return features;
    }


    @GetMapping({"/predict-latest-v2-phase2", "/predict/{modelId}"})
    public ResponseEntity<?> predict(
            @PathVariable(required = false) String modelId,
            @RequestParam(name = "modelId", required = false) String modelIdParam
    ) {
        String resolvedModelId =
                (modelId != null) ? modelId :
                        (modelIdParam != null) ? modelIdParam :
                                "lgbm_flux_27_lags_horizon_1"; // default

        logger.info("Phase2 Prediction requested with model={}", resolvedModelId);

        try {

            // Validate model exists
            ModelRegistry model = modelRegistryRepository.findById(resolvedModelId)
                    .orElseThrow(() -> new RuntimeException("Model not found: " + resolvedModelId));

            // Fetch + Cache features
            Map<String, Object> features = fetchAndCacheFeatures();
            logger.info("Successfully retrieved (and cached if needed) features");

            // Call Model Service
            String predictUrl = buildPredictUrl(modelUrl, resolvedModelId);
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

            // Determine horizon
            Integer horizonDays = extractHorizonFromModelId(resolvedModelId);
            LocalDate today = LocalDate.now();
            LocalDate targetDate = today.plusDays(horizonDays);

            Optional<Prediction> existing =
                    predictionService.findByModelAndTargetDate(model, targetDate);

            Prediction prediction;

            if (existing.isPresent()) {
                logger.info("Prediction already exists for model={} and target date={}",
                        resolvedModelId, targetDate);
                prediction = existing.get();
            } else {
                prediction = predictionService.savePrediction(
                        model,
                        today,
                        targetDate,
                        horizonDays,
                        predictedValue,
                        features,
                        "Generated via phase2 Endpoint"
                );
                logger.info("Phase2 prediction stored with ID={}",
                        prediction.getId());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("predictionId", prediction.getId());
            result.put("predictedValue", predictedValue);
            result.put("modelId", resolvedModelId);
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
    public ResponseEntity<?> getFeatures() {

        logger.info("Features requested");

        try {

            Map<String, Object> features = fetchAndCacheFeatures();
            logger.info("Successfully retrieved (and cached if needed) features");

            Map<String, Object> result = new HashMap<>();
            result.put("features", features);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("get-features failed", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Generates a prediction value via custom inputs (prediction results are not stored to the DB)
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

            logger.info("Manual Prediction received: {}", predictedValue);

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

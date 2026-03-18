package aerospaceproject.controllers;

import aerospaceproject.services.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<?> evaluateModel(@PathVariable String modelId) {
        return ResponseEntity.ok(
                evaluationService.evaluateModel(modelId)
        );
    }
}

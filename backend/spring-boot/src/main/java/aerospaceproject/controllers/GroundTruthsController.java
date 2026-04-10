package aerospaceproject.controllers;

import aerospaceproject.entities.GroundTruth;
import aerospaceproject.services.FetchServiceClient;
import aerospaceproject.services.GroundTruthService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ground-truths")
public class GroundTruthsController {

    private final GroundTruthService groundTruthService;

    private final FetchServiceClient fetchServiceClient;

    public GroundTruthsController(GroundTruthService groundTruthService,
                                  FetchServiceClient fetchServiceClient) {
        this.groundTruthService = groundTruthService;
        this.fetchServiceClient = fetchServiceClient;
    }

    // May be deprecated/modified
    @GetMapping
    public List<GroundTruth> getAll() {
        return groundTruthService.getAll();
    }

    // May be modified
    @GetMapping("/{date}")
    public GroundTruth getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // YYYY-MM-DD
            LocalDate date) {
        return groundTruthService.getByDate(date)
                .orElseThrow(() -> new RuntimeException("Ground Truth not found"));
    }

    // May be deprecated
    @PostMapping
    public GroundTruth create(@RequestBody GroundTruth groundTruth) {
        return groundTruthService.save(
                groundTruth.getObservationDate(),
                groundTruth.getActualValue(),
                groundTruth.getSourceMeta()
        );
    }

    // Endpoint to retrieve ground truth data from fetch-service
    @GetMapping("/get")
    public ResponseEntity<?> getGroundTruths(
            @RequestParam(required = false, defaultValue = "365") int days
    ) {
        return ResponseEntity.ok(
                fetchServiceClient.getGroundTruths(days)
        );
    }
}

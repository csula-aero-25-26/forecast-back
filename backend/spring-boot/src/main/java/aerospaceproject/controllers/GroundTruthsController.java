package aerospaceproject.controllers;

import aerospaceproject.entities.GroundTruth;
import aerospaceproject.services.GroundTruthService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ground-truths")
public class GroundTruthsController {

    private final GroundTruthService groundTruthService;

    public GroundTruthsController(GroundTruthService groundTruthService) {
        this.groundTruthService = groundTruthService;
    }

    @GetMapping
    public List<GroundTruth> getAll() {
        return groundTruthService.getAll();
    }

    @GetMapping("/{date}")
    public GroundTruth getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // YYYY-MM-DD
            LocalDate date) {
        return groundTruthService.getByDate(date)
                .orElseThrow(() -> new RuntimeException("Ground Truth not found"));
    }

    @PostMapping
    public GroundTruth create(@RequestBody GroundTruth groundTruth) {
        return groundTruthService.save(
                groundTruth.getObservationDate(),
                groundTruth.getActualValue(),
                groundTruth.getSourceMeta()
        );
    }
}

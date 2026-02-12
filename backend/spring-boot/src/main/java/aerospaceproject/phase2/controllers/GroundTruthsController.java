package aerospaceproject.phase2.controllers;

import aerospaceproject.phase2.entities.GroundTruths;
import aerospaceproject.phase2.services.GroundTruthsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ground-truths")
public class GroundTruthsController {

    private final GroundTruthsService groundTruthsService;

    public GroundTruthsController(GroundTruthsService groundTruthsService) {
        this.groundTruthsService = groundTruthsService;
    }

    @GetMapping
    public List<GroundTruths> getAll() {
        return groundTruthsService.getAll();
    }

    @GetMapping("/{date}")
    public GroundTruths getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // YYYY-MM-DD
            LocalDate date) {
        return groundTruthsService.getByDate(date)
                .orElseThrow(() -> new RuntimeException("Ground Truth not found"));
    }

    @PostMapping
    public GroundTruths create(@RequestBody GroundTruths groundTruth) {
        return groundTruthsService.save(
                groundTruth.getObservationDate(),
                groundTruth.getActualValue(),
                groundTruth.getSourceMeta()
        );
    }
}

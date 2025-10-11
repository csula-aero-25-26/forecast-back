package aerospaceproject;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class F107PredictionController {

    private final F107PredictionService service;

    public F107PredictionController(F107PredictionService service) {
        this.service = service;
    }

    @GetMapping
    public List<F107Prediction> getPredictions() {
        return service.getAllPredictions();
    }

    @PostMapping
    public F107Prediction createPrediction(@RequestBody F107Prediction f107Prediction) {
        return service.savePrediction(f107Prediction);
    }

}

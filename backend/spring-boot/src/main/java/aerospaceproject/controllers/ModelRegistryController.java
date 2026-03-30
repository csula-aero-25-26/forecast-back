package aerospaceproject.controllers;

import aerospaceproject.entities.ModelRegistry;
import aerospaceproject.services.ModelRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelRegistryController {

    private final ModelRegistryService service;

    public ModelRegistryController(ModelRegistryService service) {
        this.service = service;
    }

    @GetMapping
    public List<ModelRegistry> getModels() {
        return service.getAllModels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelRegistry> getOne(@PathVariable String id) {
        return service.getModel(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ModelRegistry create(@RequestBody ModelRegistry model) {
        return service.saveModel(model);
    }
}

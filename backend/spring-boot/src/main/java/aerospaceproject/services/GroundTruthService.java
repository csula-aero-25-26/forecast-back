package aerospaceproject.services;

import aerospaceproject.entities.GroundTruth;
import aerospaceproject.repositories.GroundTruthRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GroundTruthService {

    private final GroundTruthRepository groundTruthsrepo;

    public GroundTruthService(GroundTruthRepository groundTruthsrepo) {
        this.groundTruthsrepo = groundTruthsrepo;
    }

    public GroundTruth save(LocalDate date, Double actualValue, String sourceMeta) {
        GroundTruth gt = new GroundTruth(date, actualValue);
        gt.setSourceMeta(sourceMeta);
        return groundTruthsrepo.save(gt);
    }

    public Optional<GroundTruth> getByDate(LocalDate date) {
        return groundTruthsrepo.findByObservationDate(date);
    }

    public List<GroundTruth> getAll() {
        return groundTruthsrepo.findAll();
    }
}

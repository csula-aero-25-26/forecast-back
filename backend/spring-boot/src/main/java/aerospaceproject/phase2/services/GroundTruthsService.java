package aerospaceproject.phase2.services;

import aerospaceproject.phase2.entities.GroundTruths;
import aerospaceproject.phase2.repositories.GroundTruthsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GroundTruthsService {

    private final GroundTruthsRepository groundTruthsrepo;

    public GroundTruthsService(GroundTruthsRepository groundTruthsrepo) {
        this.groundTruthsrepo = groundTruthsrepo;
    }

    public GroundTruths save(LocalDate date, Double actualValue, String sourceMeta) {
        GroundTruths gt = new GroundTruths(date, actualValue);
        gt.setSourceMeta(sourceMeta);
        return groundTruthsrepo.save(gt);
    }

    public Optional<GroundTruths> getByDate(LocalDate date) {
        return groundTruthsrepo.findByObservationDate(date);
    }

    public List<GroundTruths> getAll() {
        return groundTruthsrepo.findAll();
    }
}

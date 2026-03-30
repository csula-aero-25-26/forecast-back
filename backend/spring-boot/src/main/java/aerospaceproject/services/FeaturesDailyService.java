package aerospaceproject.services;

import aerospaceproject.entities.FeaturesDaily;
import aerospaceproject.repositories.FeaturesDailyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FeaturesDailyService {

    private final FeaturesDailyRepository repository;

    public FeaturesDailyService(FeaturesDailyRepository repository) {
        this.repository = repository;
    }

    public Optional<FeaturesDaily> findByDate(LocalDate date) {
        return repository.findById(date);
    }

    public boolean exists(LocalDate date) {
        return repository.existsById(date);
    }

    public Map<String, Object> toFeatureMap(FeaturesDaily entity) {

        Map<String, Object> map = new HashMap<>();

        map.put("F10.7obs", entity.getF107Obs());
        map.put("SN", entity.getSn());

        map.put("f107_lag_1", entity.getF107Lag1());
        map.put("f107_lag_2", entity.getF107Lag2());
        map.put("f107_lag_3", entity.getF107Lag3());
        map.put("f107_lag_4", entity.getF107Lag4());
        map.put("f107_lag_5", entity.getF107Lag5());
        map.put("f107_lag_6", entity.getF107Lag6());
        map.put("f107_lag_7", entity.getF107Lag7());
        map.put("f107_lag_8", entity.getF107Lag8());
        map.put("f107_lag_9", entity.getF107Lag9());
        map.put("f107_lag_10", entity.getF107Lag10());
        map.put("f107_lag_11", entity.getF107Lag11());
        map.put("f107_lag_12", entity.getF107Lag12());
        map.put("f107_lag_13", entity.getF107Lag13());
        map.put("f107_lag_14", entity.getF107Lag14());
        map.put("f107_lag_15", entity.getF107Lag15());
        map.put("f107_lag_16", entity.getF107Lag16());
        map.put("f107_lag_17", entity.getF107Lag17());
        map.put("f107_lag_18", entity.getF107Lag18());
        map.put("f107_lag_19", entity.getF107Lag19());
        map.put("f107_lag_20", entity.getF107Lag20());
        map.put("f107_lag_21", entity.getF107Lag21());
        map.put("f107_lag_22", entity.getF107Lag22());
        map.put("f107_lag_23", entity.getF107Lag23());
        map.put("f107_lag_24", entity.getF107Lag24());
        map.put("f107_lag_25", entity.getF107Lag25());
        map.put("f107_lag_26", entity.getF107Lag26());
        map.put("f107_lag_27", entity.getF107Lag27());

        return map;
    }

    public FeaturesDaily save(LocalDate date, Map<String, Object> featuresJson) {

        FeaturesDaily entity = new FeaturesDaily();
        entity.setDate(date);

        entity.setF107Obs((Double) featuresJson.get("F10.7obs"));
        entity.setSn((Double) featuresJson.get("SN"));

        entity.setF107Lag1((Double) featuresJson.get("f107_lag_1"));
        entity.setF107Lag2((Double) featuresJson.get("f107_lag_2"));
        entity.setF107Lag3((Double) featuresJson.get("f107_lag_3"));
        entity.setF107Lag4((Double) featuresJson.get("f107_lag_4"));
        entity.setF107Lag5((Double) featuresJson.get("f107_lag_5"));
        entity.setF107Lag6((Double) featuresJson.get("f107_lag_6"));
        entity.setF107Lag7((Double) featuresJson.get("f107_lag_7"));
        entity.setF107Lag8((Double) featuresJson.get("f107_lag_8"));
        entity.setF107Lag9((Double) featuresJson.get("f107_lag_9"));
        entity.setF107Lag10((Double) featuresJson.get("f107_lag_10"));
        entity.setF107Lag11((Double) featuresJson.get("f107_lag_11"));
        entity.setF107Lag12((Double) featuresJson.get("f107_lag_12"));
        entity.setF107Lag13((Double) featuresJson.get("f107_lag_13"));
        entity.setF107Lag14((Double) featuresJson.get("f107_lag_14"));
        entity.setF107Lag15((Double) featuresJson.get("f107_lag_15"));
        entity.setF107Lag16((Double) featuresJson.get("f107_lag_16"));
        entity.setF107Lag17((Double) featuresJson.get("f107_lag_17"));
        entity.setF107Lag18((Double) featuresJson.get("f107_lag_18"));
        entity.setF107Lag19((Double) featuresJson.get("f107_lag_19"));
        entity.setF107Lag20((Double) featuresJson.get("f107_lag_20"));
        entity.setF107Lag21((Double) featuresJson.get("f107_lag_21"));
        entity.setF107Lag22((Double) featuresJson.get("f107_lag_22"));
        entity.setF107Lag23((Double) featuresJson.get("f107_lag_23"));
        entity.setF107Lag24((Double) featuresJson.get("f107_lag_24"));
        entity.setF107Lag25((Double) featuresJson.get("f107_lag_25"));
        entity.setF107Lag26((Double) featuresJson.get("f107_lag_26"));
        entity.setF107Lag27((Double) featuresJson.get("f107_lag_27"));

        return repository.save(entity);
    }

    public FeaturesDaily getOrSave(LocalDate date, Map<String, Object> featuresJson) {
        return repository.findById(date)
                .orElseGet(() -> save(date, featuresJson));
    }
}
package aerospaceproject.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Map;

public class ManualOverrideRequest {

    @JsonAlias({"modelId", "model_id"})
    private String modelId;

    private Map<String, Object> features;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
}

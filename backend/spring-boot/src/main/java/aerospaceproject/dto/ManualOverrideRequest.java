package aerospaceproject.dto;

import java.util.Map;

public class ManualOverrideRequest {

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

package aerospaceproject.services;

import aerospaceproject.dto.GroundTruthDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class FetchServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fetch.service.url}")
    private String fetchServiceUrl;

    public List<GroundTruthDTO> getGroundTruths(Integer days) {
        String url = fetchServiceUrl + "/ground-truths";

        if (days != null) {
            url += "?days=" + days;
        }

        ResponseEntity<GroundTruthDTO[]> response =
                restTemplate.getForEntity(url, GroundTruthDTO[].class);

        return Arrays.asList(response.getBody());
    }
}

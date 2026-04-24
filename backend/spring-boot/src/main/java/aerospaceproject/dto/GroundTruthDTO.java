package aerospaceproject.dto;

import java.time.LocalDate;

public class GroundTruthDTO {

    private LocalDate observation_date;
    private Double actual_flux;

    public LocalDate getObservation_date() { return observation_date; }
    public Double getActual_flux() { return actual_flux; }
}

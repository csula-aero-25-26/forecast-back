package aerospaceproject.dto;

import java.time.LocalDate;

public class PredictionHistoryDTO {

    private LocalDate date;
    private Double predicted;
    private Double actual;
    private Double error;

    public PredictionHistoryDTO(LocalDate date, Double predicted, Double actual) {
        this.date = date;
        this.predicted = predicted;
        this.actual = actual;
        this.error = actual-predicted;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getPredicted() {
        return predicted;
    }

    public Double getActual() {
        return actual;
    }

    public Double getError() {
        return error;
    }
}

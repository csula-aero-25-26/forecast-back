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

        if (predicted != null && actual != null) {
            this.error = Math.round(Math.abs(actual - predicted) * 100.0) / 100.0;
        } else {
            this.error = null;
        }
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

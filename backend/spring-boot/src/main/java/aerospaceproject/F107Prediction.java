//TODO. Had AI help with this but just a placeholder until we understand
// how the ML team structures the output

package aerospaceproject;

import jakarta.persistence.*;
import java.time.LocalDate;

/*
To get into db, run commands in terminal (Make sure docker app is open):

   docker exec -it postgres-spring-boot bash

   psql -U aspteam -d forecastdb

 */

@Entity
@Table(name = "f107_predictions")
public class F107Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "input_data_id")
    private F107InputData input;

    @Column(name = "predicted_value")
    private Double predictedValue;

    @Column(name = "model_version")
    private String modelVersion;

    public F107Prediction() {
    }

    public F107Prediction(LocalDate date, Double predictedValue, String modelVersion) {
        this.date = date;
        this.predictedValue = predictedValue;
        this.modelVersion = modelVersion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public F107InputData getInput() {
        return input;
    }

    public void setInput(F107InputData input) {
        this.input = input;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(Double predictedValue) {
        this.predictedValue = predictedValue;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}

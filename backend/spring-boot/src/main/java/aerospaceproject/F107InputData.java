package aerospaceproject;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "f107_input_data")
public class F107InputData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "f107_lag_values", joinColumns = @JoinColumn(name = "input_id"))
    @Column(name = "lag_value")
    private List<Double> lags;

    private Double ap_mean;
    private Double ap_max;

    private Double ap_mean_lag1;
    private Double ap_mean_lag2;
    private Double ap_mean_lag3;

    private Double ap_max_lag1;
    private Double ap_max_lag2;
    private Double ap_max_lag3;

    public F107InputData() {
    }

    public F107InputData(Long id, List<Double> lags, double ap_mean, Double am_max, Double ap_mean_lag1,
                         Double ap_mean_lag2, Double ap_mean_lag3, Double ap_max_lag1, Double ap_max_lag2,
                         Double ap_max_lag3) {
        this.id = id;
        this.lags = lags;
        this.ap_mean = ap_mean;
        this.ap_max = am_max;
        this.ap_mean_lag1 = ap_mean_lag1;
        this.ap_mean_lag2 = ap_mean_lag2;
        this.ap_mean_lag3 = ap_mean_lag3;
        this.ap_max_lag1 = ap_max_lag1;
        this.ap_max_lag2 = ap_max_lag2;
        this.ap_max_lag3 = ap_max_lag3;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Double> getLags() {
        return lags;
    }

    public void setLags(List<Double> lags) {
        this.lags = lags;
    }

    public Double getAp_mean() {
        return ap_mean;
    }

    public void setAp_mean(Double ap_mean) {
        this.ap_mean = ap_mean;
    }

    public Double getAp_max() {
        return ap_max;
    }

    public void setAp_max(Double ap_max) {
        this.ap_max = ap_max;
    }

    public Double getAp_mean_lag1() {
        return ap_mean_lag1;
    }

    public void setAp_mean_lag1(Double ap_mean_lag1) {
        this.ap_mean_lag1 = ap_mean_lag1;
    }

    public Double getAp_mean_lag2() {
        return ap_mean_lag2;
    }

    public void setAp_mean_lag2(Double ap_mean_lag2) {
        this.ap_mean_lag2 = ap_mean_lag2;
    }

    public Double getAp_mean_lag3() {
        return ap_mean_lag3;
    }

    public void setAp_mean_lag3(Double ap_mean_lag3) {
        this.ap_mean_lag3 = ap_mean_lag3;
    }

    public Double getAp_max_lag1() {
        return ap_max_lag1;
    }

    public void setAp_max_lag1(Double ap_max_lag1) {
        this.ap_max_lag1 = ap_max_lag1;
    }

    public Double getAp_max_lag2() {
        return ap_max_lag2;
    }

    public void setAp_max_lag2(Double ap_max_lag2) {
        this.ap_max_lag2 = ap_max_lag2;
    }

    public Double getAp_max_lag3() {
        return ap_max_lag3;
    }

    public void setAp_max_lag3(Double ap_max_lag3) {
        this.ap_max_lag3 = ap_max_lag3;
    }
}

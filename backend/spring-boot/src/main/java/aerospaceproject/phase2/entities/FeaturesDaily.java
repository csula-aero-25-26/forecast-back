package aerospaceproject.phase2.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "features_daily")
public class FeaturesDaily {

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "flux")
    private Double flux;

    @Column(name = "adj_flux")
    private Double adjFlux;

    @Column(name = "target_flux")
    private Double targetFlux;

    @Column(name = "f107_lag1") private Double f107Lag1;
    @Column(name = "f107_lag2") private Double f107Lag2;
    @Column(name = "f107_lag3") private Double f107Lag3;
    @Column(name = "f107_lag4") private Double f107Lag4;
    @Column(name = "f107_lag5") private Double f107Lag5;
    @Column(name = "f107_lag6") private Double f107Lag6;
    @Column(name = "f107_lag7") private Double f107Lag7;
    @Column(name = "f107_lag8") private Double f107Lag8;
    @Column(name = "f107_lag9") private Double f107Lag9;
    @Column(name = "f107_lag10") private Double f107Lag10;
    @Column(name = "f107_lag11") private Double f107Lag11;
    @Column(name = "f107_lag12") private Double f107Lag12;
    @Column(name = "f107_lag13") private Double f107Lag13;
    @Column(name = "f107_lag14") private Double f107Lag14;
    @Column(name = "f107_lag15") private Double f107Lag15;
    @Column(name = "f107_lag16") private Double f107Lag16;
    @Column(name = "f107_lag17") private Double f107Lag17;
    @Column(name = "f107_lag18") private Double f107Lag18;
    @Column(name = "f107_lag19") private Double f107Lag19;
    @Column(name = "f107_lag20") private Double f107Lag20;
    @Column(name = "f107_lag21") private Double f107Lag21;
    @Column(name = "f107_lag22") private Double f107Lag22;
    @Column(name = "f107_lag23") private Double f107Lag23;
    @Column(name = "f107_lag24") private Double f107Lag24;
    @Column(name = "f107_lag25") private Double f107Lag25;
    @Column(name = "f107_lag26") private Double f107Lag26;
    @Column(name = "f107_lag27") private Double f107Lag27;

    @Column(name = "ap_mean_lag1")
    private Double apMeanLag1;

    @Column(name = "ap_mean_lag2")
    private Double apMeanLag2;

    @Column(name = "ap_mean_lag3")
    private Double apMeanLag3;

    @Column(name = "ap_max_lag1")
    private Double apMaxLag1;

    @Column(name = "ap_max_lag2")
    private Double apMaxLag2;

    @Column(name = "ap_max_lag3")
    private Double apMaxLag3;

//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    protected FeaturesDaily() {}

    public FeaturesDaily(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getFlux() {
        return flux;
    }

    public void setFlux(Double flux) {
        this.flux = flux;
    }

    public Double getAdjFlux() {
        return adjFlux;
    }

    public void setAdjFlux(Double adjFlux) {
        this.adjFlux = adjFlux;
    }

    public Double getTargetFlux() {
        return targetFlux;
    }

    public void setTargetFlux(Double targetFlux) {
        this.targetFlux = targetFlux;
    }

    public Double getF107Lag1() {
        return f107Lag1;
    }

    public void setF107Lag1(Double f107Lag1) {
        this.f107Lag1 = f107Lag1;
    }

    public Double getF107Lag2() {
        return f107Lag2;
    }

    public void setF107Lag2(Double f107Lag2) {
        this.f107Lag2 = f107Lag2;
    }

    public Double getF107Lag3() {
        return f107Lag3;
    }

    public void setF107Lag3(Double f107Lag3) {
        this.f107Lag3 = f107Lag3;
    }

    public Double getF107Lag4() {
        return f107Lag4;
    }

    public void setF107Lag4(Double f107Lag4) {
        this.f107Lag4 = f107Lag4;
    }

    public Double getF107Lag5() {
        return f107Lag5;
    }

    public void setF107Lag5(Double f107Lag5) {
        this.f107Lag5 = f107Lag5;
    }

    public Double getF107Lag6() {
        return f107Lag6;
    }

    public void setF107Lag6(Double f107Lag6) {
        this.f107Lag6 = f107Lag6;
    }

    public Double getF107Lag7() {
        return f107Lag7;
    }

    public void setF107Lag7(Double f107Lag7) {
        this.f107Lag7 = f107Lag7;
    }

    public Double getF107Lag8() {
        return f107Lag8;
    }

    public void setF107Lag8(Double f107Lag8) {
        this.f107Lag8 = f107Lag8;
    }

    public Double getF107Lag9() {
        return f107Lag9;
    }

    public void setF107Lag9(Double f107Lag9) {
        this.f107Lag9 = f107Lag9;
    }

    public Double getF107Lag10() {
        return f107Lag10;
    }

    public void setF107Lag10(Double f107Lag10) {
        this.f107Lag10 = f107Lag10;
    }

    public Double getF107Lag11() {
        return f107Lag11;
    }

    public void setF107Lag11(Double f107Lag11) {
        this.f107Lag11 = f107Lag11;
    }

    public Double getF107Lag12() {
        return f107Lag12;
    }

    public void setF107Lag12(Double f107Lag12) {
        this.f107Lag12 = f107Lag12;
    }

    public Double getF107Lag13() {
        return f107Lag13;
    }

    public void setF107Lag13(Double f107Lag13) {
        this.f107Lag13 = f107Lag13;
    }

    public Double getF107Lag14() {
        return f107Lag14;
    }

    public void setF107Lag14(Double f107Lag14) {
        this.f107Lag14 = f107Lag14;
    }

    public Double getF107Lag15() {
        return f107Lag15;
    }

    public void setF107Lag15(Double f107Lag15) {
        this.f107Lag15 = f107Lag15;
    }

    public Double getF107Lag16() {
        return f107Lag16;
    }

    public void setF107Lag16(Double f107Lag16) {
        this.f107Lag16 = f107Lag16;
    }

    public Double getF107Lag17() {
        return f107Lag17;
    }

    public void setF107Lag17(Double f107Lag17) {
        this.f107Lag17 = f107Lag17;
    }

    public Double getF107Lag18() {
        return f107Lag18;
    }

    public void setF107Lag18(Double f107Lag18) {
        this.f107Lag18 = f107Lag18;
    }

    public Double getF107Lag19() {
        return f107Lag19;
    }

    public void setF107Lag19(Double f107Lag19) {
        this.f107Lag19 = f107Lag19;
    }

    public Double getF107Lag20() {
        return f107Lag20;
    }

    public void setF107Lag20(Double f107Lag20) {
        this.f107Lag20 = f107Lag20;
    }

    public Double getF107Lag21() {
        return f107Lag21;
    }

    public void setF107Lag21(Double f107Lag21) {
        this.f107Lag21 = f107Lag21;
    }

    public Double getF107Lag22() {
        return f107Lag22;
    }

    public void setF107Lag22(Double f107Lag22) {
        this.f107Lag22 = f107Lag22;
    }

    public Double getF107Lag23() {
        return f107Lag23;
    }

    public void setF107Lag23(Double f107Lag23) {
        this.f107Lag23 = f107Lag23;
    }

    public Double getF107Lag24() {
        return f107Lag24;
    }

    public void setF107Lag24(Double f107Lag24) {
        this.f107Lag24 = f107Lag24;
    }

    public Double getF107Lag25() {
        return f107Lag25;
    }

    public void setF107Lag25(Double f107Lag25) {
        this.f107Lag25 = f107Lag25;
    }

    public Double getF107Lag26() {
        return f107Lag26;
    }

    public void setF107Lag26(Double f107Lag26) {
        this.f107Lag26 = f107Lag26;
    }

    public Double getF107Lag27() {
        return f107Lag27;
    }

    public void setF107Lag27(Double f107Lag27) {
        this.f107Lag27 = f107Lag27;
    }

    public Double getApMeanLag1() {
        return apMeanLag1;
    }

    public void setApMeanLag1(Double apMeanLag1) {
        this.apMeanLag1 = apMeanLag1;
    }

    public Double getApMeanLag2() {
        return apMeanLag2;
    }

    public void setApMeanLag2(Double apMeanLag2) {
        this.apMeanLag2 = apMeanLag2;
    }

    public Double getApMeanLag3() {
        return apMeanLag3;
    }

    public void setApMeanLag3(Double apMeanLag3) {
        this.apMeanLag3 = apMeanLag3;
    }

    public Double getApMaxLag1() {
        return apMaxLag1;
    }

    public void setApMaxLag1(Double apMaxLag1) {
        this.apMaxLag1 = apMaxLag1;
    }

    public Double getApMaxLag2() {
        return apMaxLag2;
    }

    public void setApMaxLag2(Double apMaxLag2) {
        this.apMaxLag2 = apMaxLag2;
    }

    public Double getApMaxLag3() {
        return apMaxLag3;
    }

    public void setApMaxLag3(Double apMaxLag3) {
        this.apMaxLag3 = apMaxLag3;
    }
}

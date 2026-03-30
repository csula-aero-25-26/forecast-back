package aerospaceproject.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "features_daily")
public class FeaturesDaily {

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "f107_obs", nullable = false)
    private Double f107Obs;

    @Column(name = "sn")
    private Double sn;

    @Column(name = "f107_lag_1")  private Double f107Lag1;
    @Column(name = "f107_lag_2")  private Double f107Lag2;
    @Column(name = "f107_lag_3")  private Double f107Lag3;
    @Column(name = "f107_lag_4")  private Double f107Lag4;
    @Column(name = "f107_lag_5")  private Double f107Lag5;
    @Column(name = "f107_lag_6")  private Double f107Lag6;
    @Column(name = "f107_lag_7")  private Double f107Lag7;
    @Column(name = "f107_lag_8")  private Double f107Lag8;
    @Column(name = "f107_lag_9")  private Double f107Lag9;
    @Column(name = "f107_lag_10") private Double f107Lag10;
    @Column(name = "f107_lag_11") private Double f107Lag11;
    @Column(name = "f107_lag_12") private Double f107Lag12;
    @Column(name = "f107_lag_13") private Double f107Lag13;
    @Column(name = "f107_lag_14") private Double f107Lag14;
    @Column(name = "f107_lag_15") private Double f107Lag15;
    @Column(name = "f107_lag_16") private Double f107Lag16;
    @Column(name = "f107_lag_17") private Double f107Lag17;
    @Column(name = "f107_lag_18") private Double f107Lag18;
    @Column(name = "f107_lag_19") private Double f107Lag19;
    @Column(name = "f107_lag_20") private Double f107Lag20;
    @Column(name = "f107_lag_21") private Double f107Lag21;
    @Column(name = "f107_lag_22") private Double f107Lag22;
    @Column(name = "f107_lag_23") private Double f107Lag23;
    @Column(name = "f107_lag_24") private Double f107Lag24;
    @Column(name = "f107_lag_25") private Double f107Lag25;
    @Column(name = "f107_lag_26") private Double f107Lag26;
    @Column(name = "f107_lag_27") private Double f107Lag27;

    public FeaturesDaily() {}

    public FeaturesDaily(LocalDate date) {
        this.date = date;
    }

    // Getters & Setters

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getF107Obs() { return f107Obs; }
    public void setF107Obs(Double f107Obs) { this.f107Obs = f107Obs; }

    public Double getSn() { return sn; }
    public void setSn(Double sn) { this.sn = sn; }

    public Double getF107Lag1() { return f107Lag1; }
    public void setF107Lag1(Double f107Lag1) { this.f107Lag1 = f107Lag1; }

    public Double getF107Lag2() { return f107Lag2; }
    public void setF107Lag2(Double f107Lag2) { this.f107Lag2 = f107Lag2; }

    public Double getF107Lag3() { return f107Lag3; }
    public void setF107Lag3(Double f107Lag3) { this.f107Lag3 = f107Lag3; }

    public Double getF107Lag4() { return f107Lag4; }
    public void setF107Lag4(Double f107Lag4) { this.f107Lag4 = f107Lag4; }

    public Double getF107Lag5() { return f107Lag5; }
    public void setF107Lag5(Double f107Lag5) { this.f107Lag5 = f107Lag5; }

    public Double getF107Lag6() { return f107Lag6; }
    public void setF107Lag6(Double f107Lag6) { this.f107Lag6 = f107Lag6; }

    public Double getF107Lag7() { return f107Lag7; }
    public void setF107Lag7(Double f107Lag7) { this.f107Lag7 = f107Lag7; }

    public Double getF107Lag8() { return f107Lag8; }
    public void setF107Lag8(Double f107Lag8) { this.f107Lag8 = f107Lag8; }

    public Double getF107Lag9() { return f107Lag9; }
    public void setF107Lag9(Double f107Lag9) { this.f107Lag9 = f107Lag9; }

    public Double getF107Lag10() { return f107Lag10; }
    public void setF107Lag10(Double f107Lag10) { this.f107Lag10 = f107Lag10; }

    public Double getF107Lag11() { return f107Lag11; }
    public void setF107Lag11(Double f107Lag11) { this.f107Lag11 = f107Lag11; }

    public Double getF107Lag12() { return f107Lag12; }
    public void setF107Lag12(Double f107Lag12) { this.f107Lag12 = f107Lag12; }

    public Double getF107Lag13() { return f107Lag13; }
    public void setF107Lag13(Double f107Lag13) { this.f107Lag13 = f107Lag13; }

    public Double getF107Lag14() { return f107Lag14; }
    public void setF107Lag14(Double f107Lag14) { this.f107Lag14 = f107Lag14; }

    public Double getF107Lag15() { return f107Lag15; }
    public void setF107Lag15(Double f107Lag15) { this.f107Lag15 = f107Lag15; }

    public Double getF107Lag16() { return f107Lag16; }
    public void setF107Lag16(Double f107Lag16) { this.f107Lag16 = f107Lag16; }

    public Double getF107Lag17() { return f107Lag17; }
    public void setF107Lag17(Double f107Lag17) { this.f107Lag17 = f107Lag17; }

    public Double getF107Lag18() { return f107Lag18; }
    public void setF107Lag18(Double f107Lag18) { this.f107Lag18 = f107Lag18; }

    public Double getF107Lag19() { return f107Lag19; }
    public void setF107Lag19(Double f107Lag19) { this.f107Lag19 = f107Lag19; }

    public Double getF107Lag20() { return f107Lag20; }
    public void setF107Lag20(Double f107Lag20) { this.f107Lag20 = f107Lag20; }

    public Double getF107Lag21() { return f107Lag21; }
    public void setF107Lag21(Double f107Lag21) { this.f107Lag21 = f107Lag21; }

    public Double getF107Lag22() { return f107Lag22; }
    public void setF107Lag22(Double f107Lag22) { this.f107Lag22 = f107Lag22; }

    public Double getF107Lag23() { return f107Lag23; }
    public void setF107Lag23(Double f107Lag23) { this.f107Lag23 = f107Lag23; }

    public Double getF107Lag24() { return f107Lag24; }
    public void setF107Lag24(Double f107Lag24) { this.f107Lag24 = f107Lag24; }

    public Double getF107Lag25() { return f107Lag25; }
    public void setF107Lag25(Double f107Lag25) { this.f107Lag25 = f107Lag25; }

    public Double getF107Lag26() { return f107Lag26; }
    public void setF107Lag26(Double f107Lag26) { this.f107Lag26 = f107Lag26; }

    public Double getF107Lag27() { return f107Lag27; }
    public void setF107Lag27(Double f107Lag27) { this.f107Lag27 = f107Lag27; }
}
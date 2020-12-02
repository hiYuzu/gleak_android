package com.hb712.gleak_android.entity;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/20 10:28
 */
public class FactorCoefficientInfo {
    private Long id;
    private String factorName = "";
    private String cas = "";
    private double coefficient = 1.0D;
    private double moleculeValue = 12.0D;


    public FactorCoefficientInfo() {
    }

    public FactorCoefficientInfo(Long id, String factorName, String cas, double coefficient, double moleculeValue) {
        this.id = id;
        this.factorName = factorName;
        this.cas = cas;
        this.coefficient = coefficient;
        this.moleculeValue = moleculeValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public double getMoleculeValue() {
        return moleculeValue;
    }

    public void setMoleculeValue(double moleculeValue) {
        this.moleculeValue = moleculeValue;
    }
}

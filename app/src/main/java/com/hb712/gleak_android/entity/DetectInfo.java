package com.hb712.gleak_android.entity;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/23 13:47
 */
public class DetectInfo {
    private Long id;
    private String sampleName;
    private double sampleDetectValue;
    private String sampleDetectTime;

    public DetectInfo() {
    }

    public DetectInfo(Long id, String sampleName, double sampleDetectValue, String sampleDetectTime) {
        this.id = id;
        this.sampleName = sampleName;
        this.sampleDetectValue = sampleDetectValue;
        this.sampleDetectTime = sampleDetectTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public double getSampleDetectValue() {
        return sampleDetectValue;
    }

    public void setSampleDetectValue(double sampleDetectValue) {
        this.sampleDetectValue = sampleDetectValue;
    }

    public String getSampleDetectTime() {
        return sampleDetectTime;
    }

    public void setSampleDetectTime(String sampleDetectTime) {
        this.sampleDetectTime = sampleDetectTime;
    }
}

package com.hb712.gleak_android.controller;

import com.hb712.gleak_android.entity.FactorCoefficientInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 仪器控制、仪器信息
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/20 10:28
 */
public class DeviceController {

    private static DeviceController instance;

    public static synchronized DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

    private boolean isFireOn = false;
    private double powerPercent;
    private double vol;
    private double pump;
    private double dischargePress;
    private double outletHydrogenPress;
    private double systemCurrent;
    private double hydrogen;
    private double hydrogenPress;
    private double hydrogenPressPercent;
    private double ccTemp;
    private double fireTemp;
    private double microCurrent;

    private List<Double> pastPpm = new ArrayList<>();

    public void parseParam(byte[] paramBytes) {
        // TODO: hiYuzu 2020/12/2 解析蓝牙信息
        setPowerPercent(8D);
    }

    public void changeFactor(FactorCoefficientInfo paramFactorCoefficientInfo) {
        CalibrationInfoController.getInstance().setFactor(paramFactorCoefficientInfo);
        pastPpm.clear();
    }

    public boolean isFireOn() {
        return isFireOn;
    }

    public void setFireOn(boolean fireOn) {
        isFireOn = fireOn;
    }

    public double getPowerPercent() {
        return powerPercent;
    }

    public void setPowerPercent(double vol) {
        if (vol > 8.2D) {
            powerPercent = 100.0D;
        } else if (vol < 6.1D) {
            powerPercent = 0.0D;
        } else {
            double d = 100.0D / (8.2D - 6.1D);
            powerPercent = d * vol + (0.0D - 6.1D * d);
        }
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public double getPump() {
        return pump;
    }

    public void setPump(double pump) {
        this.pump = pump;
    }

    public double getDischargePress() {
        return dischargePress;
    }

    public void setDischargePress(double dischargePress) {
        this.dischargePress = dischargePress;
    }

    public double getOutletHydrogenPress() {
        return outletHydrogenPress;
    }

    public void setOutletHydrogenPress(double outletHydrogenPress) {
        this.outletHydrogenPress = outletHydrogenPress;
    }

    public double getSystemCurrent() {
        return systemCurrent;
    }

    public void setSystemCurrent(double systemCurrent) {
        this.systemCurrent = systemCurrent;
    }

    public double getHydrogen() {
        return hydrogen;
    }

    public void setHydrogen(double hydrogen) {
        this.hydrogen = hydrogen;
    }

    public double getHydrogenPress() {
        return hydrogenPress;
    }

    public void setHydrogenPress(double hydrogenPress) {
        this.hydrogenPress = hydrogenPress;
    }

    public double getHydrogenPressPercent() {
        return hydrogenPressPercent;
    }

    public void setHydrogenPressPercent(double hydrogenPress) {
        if (hydrogenPress > 2200.0D) {
            hydrogenPressPercent = 100.0D;
        } else if (hydrogenPress < 30.0D) {
            hydrogenPressPercent = 0.0D;
        } else {
            double d = 100.0D / (2200.0D - 30.0D);
            hydrogenPressPercent = d * hydrogenPress + (0.0D - 30.0D * d);
        }

    }

    public double getCcTemp() {
        return ccTemp;
    }

    public void setCcTemp(double ccTemp) {
        this.ccTemp = ccTemp;
    }

    public double getFireTemp() {
        return fireTemp;
    }

    public void setFireTemp(double fireTemp) {
        this.fireTemp = fireTemp;
    }

    public double getMicroCurrent() {
        return microCurrent;
    }

    public void setMicroCurrent(double microCurrent) {
        this.microCurrent = microCurrent;
    }
}


package com.hb712.gleak_android.controller;

import android.support.annotation.Nullable;

import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.util.BluetoothUtil;
import com.hb712.gleak_android.util.ByteUtil;
import com.hb712.gleak_android.util.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 仪器控制、仪器信息
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/20 10:28
 */
public class DeviceController {
    private static final String TAG = DeviceController.class.getSimpleName();

    private static DeviceController instance;

    public static synchronized DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

    private boolean fireOn = false;

    private double powerPercent;
    private double vol;
    private double pump;
    private double dischargePress;
    private double outletHydrogenPress;
    private double systemCurrent;
    private double hydrogenPress;
    private double hydrogenPressPercent;
    private double ccTemp;
    private double fireTemp;
    private double microCurrent;

    private double currentValue = 0.0D;
    private String deviceName;

    private Voc3000Status status = null;
    private final int shortAverageCount = 5;
    private byte currentHardwareAvg = 10;

    private int ignitedChagedCount = 0;
    private int junkDataCount;
    private int num0s = 0;
    private List<Double> pastPpmList = new ArrayList();
    private boolean prevIgnite;
    private double pumppower = 0.0D;
    private int zeroCount = 0;

    private List<Double> pastPpm = new ArrayList<>();

    /**
     * 解析接收的检测数据
     *
     * @param dataArray
     */
    public void analysisCommand(byte[] dataArray) {
        int minLength = 2;
        int index = 2;
        byte a = 37;
        if (dataArray != null && dataArray.length > minLength) {
            if (dataArray[index] == a) {
                // 0x25
                processData(dataArray);
            }
        }
    }

    private void processData(byte[] dataArray) {
        Voc3000Status status = parseStatus(dataArray);
        if (status != null) {
            microCurrent = status.microCurrent;
            pump = status.pump;
            hydrogenPress = status.hydrogenPress;
            vol = status.vol;
            setPowerPercent(status.vol);
            setHydrogenPressPercent(status.hydrogenPress);
            fireTemp = status.fireTemp;
            ccTemp = status.ccTemp;
            dischargePress = status.dischargePress;
            outletHydrogenPress = status.outletHydrogenPress;
            systemCurrent = status.systemCurrent;

            fireOn = status.isFireOn;
            if (powerPercent < 0) {
                powerPercent = 0;
            }
            if (fireOn) {
                currentValue = status.detectValue;
            } else {
                currentValue = 0.0D;
            }
        }
    }

    @Nullable
    private Voc3000Status parseStatus(byte[] dataArray) {
        Voc3000Status status = null;
        if (dataArray != null && dataArray.length > 38) {
            if (dataArray[2] == 37) {
                status = new Voc3000Status();
                double d = ByteUtil.bytesToDword(dataArray[35], dataArray[34], dataArray[33], dataArray[32]);
                d *= 0.1D;
                if (d >= 100.0D) {
                    d = (int) d;
                }
                if (d < 0.0D) {
                    d = 0.0D;
                }
                if (d == 0.0D) {
                    num0s++;
                    if (num0s > 5) {
                        num0s = -5;
                    }
                    if (num0s < 0) {
                        d = 0.1D;
                    }
                }
                status.isPumpAOn = (dataArray[4] & 0x1) > 0;
                status.outletHydrogenPress = (ByteUtil.bytesToWord(dataArray[16], dataArray[15]) * 0.01F);
                status.vol = (ByteUtil.bytesToWord(dataArray[10], dataArray[9]) * 0.001F);
                status.ccTemp = convertKelvinToFahrenheit1(ByteUtil.bytesToWord(dataArray[12], dataArray[11]) * 0.1F);
                status.dischargePress = (ByteUtil.bytesToWord(dataArray[14], dataArray[13]) * 0.01F);
                status.hydrogenPress = (ByteUtil.bytesToWord(dataArray[18], dataArray[17]) * 1.0F);
                status.fireTemp = convertKelvinToFahrenheit(ByteUtil.bytesToWord(dataArray[8], dataArray[7]) * 0.1F);
                d = ByteUtil.bytesToDword(dataArray[27], dataArray[26], dataArray[25], dataArray[24]);
                status.microCurrent = (d * 0.1D);
                status.systemCurrent = ByteUtil.bytesToWord(dataArray[37], dataArray[36]);
                status.pump = dataArray[38];
                status.isSolenoidAOn = (dataArray[4] & 0x4) > 0;
                status.isSolenoidBOn = (dataArray[4] & 0x8) > 0;
                status.fidRange = dataArray[19];

                boolean bool = checkIfIgnited(status);
                if (bool != prevIgnite) {
                    ignitedChagedCount += 1;
                    if (ignitedChagedCount >= 3) {
                        prevIgnite = bool;
                    }
                } else {
                    ignitedChagedCount = 0;
                }
                status.isFireOn = prevIgnite;

                if (status.vol > 15.0F || status.microCurrent < -10000.0D || status.fireTemp < -400.0F) {
                    int i = junkDataCount;
                    if (i < 10) {
                        junkDataCount = (i + 1);
                        return null;
                    }
                }
                junkDataCount = 0;
                if (status.fidRange == 0 && status.microCurrent >= 6500.0D) {
                    BluetoothUtil.getInstance().setSampleParameters(3);
                    try {
                        Thread.sleep(250L);
                    } catch (InterruptedException ie) {
                        LogUtil.warnOut(TAG, ie, null);
                    }
                } else if (status.fidRange == 3 && status.microCurrent <= 6000.0D) {
                    BluetoothUtil.getInstance().setSampleParameters(0);
                    try {
                        Thread.sleep(250L);
                    } catch (InterruptedException ie) {
                        LogUtil.warnOut(TAG, ie, null);
                    }
                }
                status.rawPpm = getValueByKB(status.microCurrent);
                pastPpmList.add(status.rawPpm);
                int maxSize = 50;
                if (pastPpmList.size() > maxSize) {
                    pastPpmList.remove(0);
                }
                int longAverageCount = 25;
                status.longAveragePpm = getAverage(longAverageCount);
                status.shortAveragePpm = getAverage(shortAverageCount);
                status.useAverage = checkP(status, pastPpmList);
                if (status.useAverage) {
                    if (status.fidRange == 3) {
                        d = status.longAveragePpm;
                    } else {
                        d = status.shortAveragePpm;
                    }
                    status.detectValue = d;
                    LogUtil.debugOut(TAG, "use average ppm: " + status.detectValue);
                } else {
                    status.detectValue = status.rawPpm;
                    LogUtil.debugOut(TAG, "use raw ppm: " + status.detectValue);
                }
                if (status.microCurrent <= 200.0D && currentHardwareAvg == 10) {
                    currentHardwareAvg = 50;
                    BluetoothUtil.getInstance().setIntegrationControlParams(currentHardwareAvg);
                } else if (status.microCurrent > 200.0D && currentHardwareAvg == 50) {
                    currentHardwareAvg = 10;
                    BluetoothUtil.getInstance().setIntegrationControlParams(currentHardwareAvg);
                }
            }
        }
        this.status = status;
        return status;
    }

    private float convertKelvinToFahrenheit1(float paramFloat) {
        return (float) Math.round((paramFloat - 273.15F) * 1.6F + 32.0F);
    }

    private float convertKelvinToFahrenheit(float paramFloat) {
        return (float) Math.round((paramFloat - 273.15F) * 2.5F + 32.0F);
    }

    /**
     * 判断是否点火成功
     *
     * @param status
     * @return
     */
    private boolean checkIfIgnited(Voc3000Status status) {
        return status.fireTemp > 75.0F && status.isSolenoidAOn && status.isPumpAOn;
    }

    /**
     * 通过K、B值计算ppm
     *
     * @param microCurrent 微电流
     * @return ppm
     */
    private double getValueByKB(double microCurrent) {
        double minPpm = 0.001D;
        double ppm = CalibrationInfoController.getInstance().getValueBySignal(microCurrent);
        if (ppm < minPpm) {
            this.zeroCount += 1;
            if (this.zeroCount % 5 == 0) {
                this.zeroCount = 0;
                return 0.1D;
            }
            return ppm;
        }
        return ppm;
    }

    private double getAverage(int averageCount) {
        if (pastPpmList.size() > 0) {
            double d1 = 0.0D;
            int i = 0;
            if (pastPpmList.size() > averageCount) {
                averageCount = pastPpmList.size() - averageCount;
            } else {
                averageCount = i;
            }
            i = 0;
            while (averageCount < pastPpmList.size()) {
                i += 1;
                d1 += pastPpmList.get(averageCount);
                averageCount += 1;
            }
            double d2 = i;
            return BigDecimal.valueOf(d1 / d2).setScale(1, 4).doubleValue();
        }
        return 0.0D;
    }

    private boolean checkP(Voc3000Status status, List<Double> pastPpmList) {
        boolean bool = false;
        if (pastPpmList != null) {
            int i = 0;
            if (pastPpmList.size() > shortAverageCount) {
                i = pastPpmList.size() - shortAverageCount;
            }
            pastPpmList = pastPpmList.subList(i, pastPpmList.size());
            for (double d : pastPpmList) {
                int useAvgPerc = 10;
                bool = d / status.longAveragePpm * 100.0D >= 100 - useAvgPerc && d / status.longAveragePpm * 100.0D <= useAvgPerc + 100;
                if (!bool) {
                    return bool;
                }
            }
        }
        return bool;
    }

    public void changeFactor(FactorCoefficientInfo paramFactorCoefficientInfo) {
        CalibrationInfoController.getInstance().setFactor(paramFactorCoefficientInfo);
        pastPpm.clear();
    }

    public boolean isFireOn() {
        return fireOn;
    }

    public double getPowerPercent() {
        return powerPercent;
    }

    public void setPowerPercent(double vol) {
        if (vol > 8.2D) {
            powerPercent = 100.0D;
        }
        if (vol < 6.1D) {
            powerPercent = 0.0D;
        }
        double d = 100.0D / (8.2D - 6.1D);
        powerPercent = d * vol + (0.0D - 6.1D * d);
    }

    public double getVol() {
        return vol;
    }

    public double getPump() {
        return pump;
    }

    public double getDischargePress() {
        return dischargePress;
    }


    public double getOutletHydrogenPress() {
        return outletHydrogenPress;
    }

    public double getSystemCurrent() {
        return systemCurrent;
    }

    public double getHydrogenPress() {
        return hydrogenPress;
    }

    public double getHydrogenPressPercent() {
        return hydrogenPressPercent;
    }

    public void setHydrogenPressPercent(double hydrogenPress) {
        if (hydrogenPress > 2200.0D) {
            hydrogenPressPercent = 100.0D;
        }
        if (hydrogenPress < 30.0D) {
            hydrogenPressPercent = 0.0D;
        }
        double d = 100.0D / (2200.0D - 30.0D);
        hydrogenPressPercent = d * hydrogenPress + (0.0D - 30.0D * d);

    }

    public double getCcTemp() {
        return ccTemp;
    }

    public double getFireTemp() {
        return fireTemp;
    }

    public double getMicroCurrent() {
        return microCurrent;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public Voc3000Status getStatus() {
        return status;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public class Voc3000Status {
        public float outletHydrogenPress;
        public float vol;
        public float ccTemp;
        public double microCurrent;
        public float systemCurrent;
        public float hydrogenPress;
        public float fireTemp;
        public float dischargePress;
        public float pump;

        public boolean isFireOn;

        public double rawPpm;
        public double detectValue;

        public boolean isPumpAOn;
        public boolean isSolenoidAOn;
        public boolean isSolenoidBOn;

        public boolean useAverage;
        public double shortAveragePpm;
        public double longAveragePpm;

        public byte fidRange;
    }
}


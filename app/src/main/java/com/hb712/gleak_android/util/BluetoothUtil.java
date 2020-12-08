package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hb712.gleak_android.message.blue.Phx21Status;
import com.hb712.gleak_android.service.BluetoothService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/25 10:56
 */
public class BluetoothUtil {
    private final String TAG = BluetoothUtil.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService = null;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private boolean fireOn = false;
    boolean isInit = false;
    private byte currentHardwareAvg = 10;
    private int num0s = 0;
    private boolean prevIgnite;
    private int ignitedChagedCount = 0;
    private int junkDataCount;
    private int changeCount = 0;
    private int zeroCount = 0;
    private List<Double> pastPpms = new ArrayList();
    private int maxPastPpms = 50;
    private int LongAverageCount = 25;
    private int ShortAverageCount = 5;
    private int UseAvgPerc = 10;
    Phx21Status _currentStatus = null;
    private double currentValue = 0.0D;
    private double h2pressure = 0.0D;
    private double h2pressurePercent = 0.0D;
    private double BatteryVoltage = 0.0D;
    private double BatteryVoltagePercent = 0.0D;
    List<PpmCalibrationInfo> PpmCalibrationInfoList = new ArrayList();
    private double picoAmps = 0.0D;
    private double pumpPower = 0.0D;

    private static BluetoothUtil instance;

    public boolean getFireOn() {
        return fireOn;
    }

    public static synchronized BluetoothUtil getInstance() {
        if (instance == null) {
            instance = new BluetoothUtil();
        }
        return instance;
    }

    //获取蓝牙adapter
    private BluetoothUtil() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public interface BluetoothListener {
        default void onDeviceConnected(String name, String address) {
            ToastUtil.shortInstanceToast("蓝牙已连接");
        }

        default void onDeviceDisconnected() {
            ToastUtil.shortInstanceToast("蓝牙已断开");
        }

        default void onDeviceConnectionFailed() {
            ToastUtil.shortInstanceToast("蓝牙连接失败");
        }

        void onDataReceived(byte[] data);
    }

    @SuppressLint("HardwareIds")
    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress() == null)
                return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isServiceAvailable() {
        return mBluetoothService != null;
    }

    public void setupService() {
        mBluetoothService = new BluetoothService(mHandler);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlobalParam.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf != null && readBuf.length > 0) {
                        onDataReceived(readBuf);
                    }
                    break;
                case GlobalParam.MESSAGE_DEVICE_NAME:
                    String mDeviceName = msg.getData().getString(GlobalParam.DEVICE_NAME);
                    String mDeviceAddress = msg.getData().getString(GlobalParam.DEVICE_ADDRESS);
                    onDeviceConnected(mDeviceName, mDeviceAddress);
                    isConnected = true;
                    break;
                case GlobalParam.MESSAGE_STATE_CHANGE:
                    if (isConnected && msg.arg1 != GlobalParam.STATE_CONNECTED) {
                        onDeviceDisconnected();
                        isConnected = false;
                    }
                    if (!isConnecting && msg.arg1 == GlobalParam.STATE_CONNECTING) {
                        isConnecting = true;
                    } else if (isConnecting) {
                        if (msg.arg1 != GlobalParam.STATE_CONNECTED) {
                            onDeviceConnectionFailed();
                        }
                        isConnecting = false;
                    }
                    break;
            }
        }
    };

    public void connect(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            LogUtil.debugOut(TAG, "无参数，连接失败");
            return;
        }
        String address = bundle.getString(GlobalParam.DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothService.connect(device);
    }

    public void disconnect() {
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }


    Map<String, BluetoothListener> listenerMap = new HashMap<>();

    public void addBluetoothListener(String name, BluetoothListener listener) {
        if (!listenerMap.containsKey(name)) {
            listenerMap.put(name, listener);
        }
    }

    public void removeBlueListener(String name) {
        listenerMap.remove(name);
    }

    private void onDataReceived(byte[] data) {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDataReceived(data);
        }
    }

    private void onDeviceConnected(String name, String address) {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDeviceConnected(name, address);
        }
    }

    void onDeviceDisconnected() {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDeviceDisconnected();
        }
    }

    void onDeviceConnectionFailed() {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDeviceConnectionFailed();
        }
    }

    public void readData() {
        init();
        writeByte(new byte[]{90, 4, 37, 65});
    }

    public void openFire() {
        writeByte(new byte[]{90, 27, 32, 1, -81, 0, 5, 0, 10, 0, 16, 39, -120, 19, -24, 3, -120, 19, -120, 19, 0, 0, 0, 0, 0, 0, 115});
        this.fireOn = true;
    }

    public void openFire2() {
        writeByte(new byte[]{90, 27, 32, 1, -81, 0, 5, 0, 10, 0, 16, 39, -120, 19, -24, 3, -120, 19, -120, 19, 0, 0, 0, 0, 1, 0, 117});
        this.fireOn = true;
    }

    public void closeFire() {
        openFire();
        try {
            Thread.sleep(300L);
        } catch (InterruptedException localInterruptedException) {
            localInterruptedException.printStackTrace();
        }
        writeByte(new byte[]{90, 27, 32, 0, -81, 0, 5, 0, 10, 0, 16, 39, -120, 19, -24, 3, -120, 19, -120, 19, 0, 0, 0, 0, 0, 0, -77});
        this.fireOn = false;
    }

    public void writeByte(byte[] paramArrayOfByte) {
        if (paramArrayOfByte != null) {
            try {
                if (paramArrayOfByte.length > 0) {
                    mBluetoothService.write(paramArrayOfByte);
                }
            } finally {
            }
        } else {
            return;
        }
    }

    private void init() {
        if (!this.isInit) {
            SetSampleParameters(0);
            SetIntegrationControlParams(this.currentHardwareAvg);
            SetDeadHeadParams();
            SetCalH2PressureCompensation();
            this.isInit = true;
            return;
        }
    }

    private void SetSampleParameters(int paramInt) {
        byte[] arrayOfByte = new byte[5];
        arrayOfByte[0] = 90;
        arrayOfByte[1] = 5;
        arrayOfByte[2] = 4;
        arrayOfByte[3] = ((byte) paramInt);
        arrayOfByte[4] = 0;
        arrayOfByte[(arrayOfByte.length - 1)] = CRC8.calcCrc(arrayOfByte, arrayOfByte.length - 1);
        writeByte(arrayOfByte);
    }

    public void SetIntegrationControlParams(int paramInt) {
        this.currentHardwareAvg = ((byte) paramInt);
        byte[] arrayOfByte = new byte[11];
        arrayOfByte[0] = 90;
        arrayOfByte[1] = 11;
        arrayOfByte[2] = 12;
        arrayOfByte[3] = 0;
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 7;
        arrayOfByte[6] = 80;
        arrayOfByte[7] = -61;
        arrayOfByte[8] = ((byte) paramInt);
        arrayOfByte[9] = 0;
        arrayOfByte[10] = 0;
        arrayOfByte[(arrayOfByte.length - 1)] = CRC8.calcCrc(arrayOfByte, arrayOfByte.length - 1);
        writeByte(arrayOfByte);
    }

    private void SetDeadHeadParams() {
        writeByte(new byte[]{90, 9, 30, 1, (byte) 150, 0, 100, 0, 20});
    }

    private void SetCalH2PressureCompensation() {
        writeByte(new byte[]{90, 25, 36, 0, 0, 0, 0, 0, -72, 11, 0, 0, 0, 0, 0, 0, 72, -12, -1, -1, -1, -1, -1, -1});
    }

    public void analysisCommand(byte[] paramArrayOfByte) {
        if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 2)) {
            int i = paramArrayOfByte[2];
            processData(paramArrayOfByte);
            if (i != 35) {
                if (i != 37) {
                    return;
                }
                processData(paramArrayOfByte);
                return;
            }
            processGETPPMCALIBRATION(paramArrayOfByte);
            return;
        }
    }

    private void processData(byte[] paramArrayOfByte) {
        Phx21Status phx21Status = parseStatus(paramArrayOfByte);
        if (phx21Status != null) {
            this.picoAmps = phx21Status.PicoAmps;
            this.pumpPower = phx21Status.PumpPower;
            this.h2pressure = phx21Status.TankPressure;
            this.BatteryVoltage = phx21Status.BatteryVoltage;
            this.BatteryVoltagePercent = getBatteryPercent(this.BatteryVoltage);
            if (this.BatteryVoltagePercent > 100.0D) {
                this.BatteryVoltagePercent = 100.0D;
            }
            this.h2pressurePercent = getH2Percent(this.h2pressure);
            if (this.h2pressurePercent > 100.0D) {
                this.h2pressurePercent = 100.0D;
            }
            this.fireOn = phx21Status.IsIgnited;
            if (this.fireOn) {
                this.currentValue = phx21Status.Ppm;
                return;
            }
            this.currentValue = 0.0D;
            return;
        }
    }

    private void processGETPPMCALIBRATION(byte[] paramArrayOfByte) {
        if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 42) && (paramArrayOfByte[2] == 35)) {
            PpmCalibrationInfo localPpmCalibrationInfo = new PpmCalibrationInfo();
            localPpmCalibrationInfo.Index = paramArrayOfByte[31];
            double d = ByteUtil.BytesToDword(paramArrayOfByte[35], paramArrayOfByte[34], paramArrayOfByte[33], paramArrayOfByte[32]);
            Double.isNaN(d);
            localPpmCalibrationInfo.Ppm = ((int) (d * 0.1D));
            localPpmCalibrationInfo.FidCurrent = ByteUtil.BytesToDword(paramArrayOfByte[39], paramArrayOfByte[38], paramArrayOfByte[37], paramArrayOfByte[36]);
            int i = ByteUtil.BytesToWord(paramArrayOfByte[41], paramArrayOfByte[40]);
            if (i < 0) {
                i += 65536;
            }
            localPpmCalibrationInfo.H2Pressure = (i * 0.01F);
            boolean bool;
            if (paramArrayOfByte[42] > 0) {
                bool = true;
            } else {
                bool = false;
            }
            localPpmCalibrationInfo.IsValid = bool;
            if (localPpmCalibrationInfo.IsValid) {
                this.PpmCalibrationInfoList.add(localPpmCalibrationInfo);
                return;
            }
            return;
        }
    }

    private Phx21Status parseStatus(byte[] paramArrayOfByte) {
        boolean bool = false;
        Phx21Status localPhx21Status = null;
        if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 38)) {
            if (paramArrayOfByte[2] == 37) {
                localPhx21Status = new Phx21Status();
                double d = ByteUtil.BytesToDword(paramArrayOfByte[35], paramArrayOfByte[34], paramArrayOfByte[33], paramArrayOfByte[32]);
                Double.isNaN(d);
                d *= 0.1D;
                if (d >= 100.0D) {
                    d = (int) d;
                }
                if (d < 0.0D) {
                    d = 0.0D;
                }
                if (d == 0.0D) {
                    this.num0s += 1;
                    if (this.num0s > 5) {
                        this.num0s = -5;
                    }
                    if (this.num0s < 0) {
                        d = 0.1D;
                    } else {
                    }
                }
                if ((paramArrayOfByte[4] & 0x1) > 0) {
                    bool = true;
                } else {
                    bool = false;
                }
                localPhx21Status.IsPumpAOn = bool;
                localPhx21Status.AirPressure = (ByteUtil.BytesToWord(paramArrayOfByte[16], paramArrayOfByte[15]) * 0.01F);
                localPhx21Status.BatteryVoltage = (ByteUtil.BytesToWord(paramArrayOfByte[10], paramArrayOfByte[9]) * 0.001F);
                localPhx21Status.ChamberOuterTemp = ConvertKelvinToFahrenheit1(ByteUtil.BytesToWord(paramArrayOfByte[12], paramArrayOfByte[11]) * 0.1F);
                localPhx21Status.RawPpm = d;
                localPhx21Status.SamplePressure = (ByteUtil.BytesToWord(paramArrayOfByte[14], paramArrayOfByte[13]) * 0.01F);
                localPhx21Status.TankPressure = (ByteUtil.BytesToWord(paramArrayOfByte[18], paramArrayOfByte[17]) * 1.0F);
                localPhx21Status.ThermoCouple = ConvertKelvinToFahrenheit(ByteUtil.BytesToWord(paramArrayOfByte[8], paramArrayOfByte[7]) * 0.1F);
                d = ByteUtil.BytesToDword(paramArrayOfByte[27], paramArrayOfByte[26], paramArrayOfByte[25], paramArrayOfByte[24]);
                Double.isNaN(d);
                localPhx21Status.PicoAmps = (d * 0.1D);
                localPhx21Status.SystemCurrent = ByteUtil.BytesToWord(paramArrayOfByte[37], paramArrayOfByte[36]);
                localPhx21Status.PumpPower = paramArrayOfByte[38];
                if ((paramArrayOfByte[4] & 0x4) > 0) {
                    bool = true;
                } else {
                    bool = false;
                }
                localPhx21Status.IsSolenoidAOn = bool;
                if ((paramArrayOfByte[4] & 0x8) > 0) {
                    bool = true;
                } else {
                    bool = false;
                }
                localPhx21Status.IsSolenoidBOn = bool;
                localPhx21Status.FIDRange = paramArrayOfByte[19];
                bool = CheckIfIgnited(localPhx21Status);
                if (bool != this.prevIgnite) {
                    this.ignitedChagedCount += 1;
                    if (this.ignitedChagedCount >= 3) {
                        this.prevIgnite = bool;
                    }
                } else {
                    this.ignitedChagedCount = 0;
                }
                localPhx21Status.IsIgnited = this.prevIgnite;
                if ((localPhx21Status.IsIgnited) && (localPhx21Status.PumpPower >= 85.0D)) {
                    TurnOffPump();
                }
                if ((localPhx21Status.BatteryVoltage > 15.0F) || (localPhx21Status.PicoAmps < -10000.0D) || (localPhx21Status.ThermoCouple < -400.0F)) {
                    int i = this.junkDataCount;
                    if (i < 10) {
                        this.junkDataCount = (i + 1);
                        readData();
                        return null;
                    }
                }
                this.junkDataCount = 0;
                if ((localPhx21Status.FIDRange == 0) && (localPhx21Status.PicoAmps >= 6500.0D)) {
                    this.changeCount += 1;
                    if (this.changeCount >= 1) {
                        this.changeCount = 0;
                        SetSampleParameters(3);
                        try {
                            Thread.sleep(250L);
                        } catch (InterruptedException ex) {
                            for (; ; ) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } else if ((localPhx21Status.FIDRange == 3) && (localPhx21Status.PicoAmps <= 6000.0D)) {
                    this.changeCount += 1;
                    if (this.changeCount >= 1) {
                        this.changeCount = 0;
                        SetSampleParameters(0);
                        try {
                            Thread.sleep(250L);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                    }
                }
                localPhx21Status.RawPpm = getValueByKB(localPhx21Status.PicoAmps);
                this.pastPpms.add(Double.valueOf(localPhx21Status.RawPpm));
                if (this.pastPpms.size() > this.maxPastPpms) {
                    this.pastPpms.remove(0);
                }
                localPhx21Status.LongAveragePpm = getAverage(this.LongAverageCount);
                localPhx21Status.ShortAveragePpm = getAverage(this.ShortAverageCount);
                localPhx21Status.UseAverage = checkP(localPhx21Status, this.pastPpms);
                if (localPhx21Status.UseAverage) {
                    if (localPhx21Status.FIDRange == 3) {
                        d = localPhx21Status.LongAveragePpm;
                    } else {
                        d = localPhx21Status.ShortAveragePpm;
                    }
                    localPhx21Status.Ppm = d;
                } else {
                    localPhx21Status.Ppm = localPhx21Status.RawPpm;
                }
                if (localPhx21Status.IsIgnited) {
                    localPhx21Status.PpmStr = String.valueOf(localPhx21Status.Ppm);
                } else {
                    localPhx21Status.PpmStr = "N/A";
                }
//                localPhx21Status = paramArrayOfByte;
                if ((localPhx21Status.PicoAmps <= 200.0D) && (this.currentHardwareAvg == 10)) {
                    SetIntegrationControlParams(50);
                } else if ((localPhx21Status.PicoAmps > 200.0D) && (this.currentHardwareAvg == 50)) {
                    SetIntegrationControlParams(10);
                }
                localPhx21Status = localPhx21Status;
            } else {
//                paramArrayOfByte = localPhx21Status;
            }
        } else {
//            paramArrayOfByte = localPhx21Status;
        }
        this._currentStatus = localPhx21Status;
        return localPhx21Status;
    }

    private float ConvertKelvinToFahrenheit1(float paramFloat) {
        return (float) Math.round((paramFloat - 273.15F) * 1.6F + 32.0F);
    }

    private float ConvertKelvinToFahrenheit(float paramFloat) {
        return (float) Math.round((paramFloat - 273.15F) * 2.5F + 32.0F);
    }

    private boolean CheckIfIgnited(Phx21Status paramPhx21Status) {
        return (paramPhx21Status.ThermoCouple > 75.0F) && (paramPhx21Status.IsSolenoidAOn) && (paramPhx21Status.IsPumpAOn);
    }

    private void TurnOffPump() {
    }

    private double getValueByKB(double paramDouble) {
        paramDouble = CalibrateInfoManager.get_mng().getValueBySignal(paramDouble);
        if (paramDouble < 0.001D) {
            this.zeroCount += 1;
            if (this.zeroCount % 5 == 0) {
                this.zeroCount = 0;
                return 0.1D;
            }
            return paramDouble;
        }
        return paramDouble;
    }

    private double getAverage(int paramInt) {
        if (this.pastPpms.size() > 0) {
//            MyLogger localMyLogger = MyLogger.jLog();
//            StringBuilder localStringBuilder = new StringBuilder();
//            localStringBuilder.append("size ");
//            localStringBuilder.append(String.valueOf(this.pastPpms.size()));
//            localStringBuilder.append(" length ");
//            localStringBuilder.append(String.valueOf(paramInt));
//            localMyLogger.saveToFile(localStringBuilder.toString());
            double d1 = 0.0D;
            int i = 0;
            if (this.pastPpms.size() > paramInt) {
                paramInt = this.pastPpms.size() - paramInt;
            } else {
                paramInt = i;
            }
            i = 0;
            while (paramInt < this.pastPpms.size()) {
                i += 1;
                d1 += ((Double) this.pastPpms.get(paramInt)).doubleValue();
                paramInt += 1;
            }
            double d2 = i;
            Double.isNaN(d2);
            return BigDecimal.valueOf(d1 / d2).setScale(1, 4).doubleValue();
        }
        return 0.0D;
    }

    boolean checkP(Phx21Status paramPhx21Status, List<Double> paramList) {
        boolean bool = false;
        if (paramList != null) {
            int i = 0;
            if (this.pastPpms.size() > this.ShortAverageCount) {
                i = this.pastPpms.size() - this.ShortAverageCount;
            }
            paramList = paramList.subList(i, paramList.size());
            if (paramList != null) {
                for (double d : paramList) {
                    if ((d / paramPhx21Status.LongAveragePpm * 100.0D >= 100 - this.UseAvgPerc) && (d / paramPhx21Status.LongAveragePpm * 100.0D <= this.UseAvgPerc + 100)) {
                        bool = true;
                    } else {
                        bool = false;
                    }
                    if (!bool) {
                        break;
                    }
                }
                return bool;
            }
            return false;
        }
        return false;
    }

    private double getBatteryPercent(double paramDouble) {
        if (paramDouble > 8.2D) {
            return 100.0D;
        }
        if (paramDouble < 6.1D) {
            return 0.0D;
        }
        double d = 100.0D / (8.2D - 6.1D);
        return d * paramDouble + (0.0D - 6.1D * d);
    }

    private double getH2Percent(double paramDouble) {
        if (paramDouble > 2200.0D) {
            return 100.0D;
        }
        if (paramDouble < 30.0D) {
            return 0.0D;
        }
        double d = 100.0D / (2200.0D - 30.0D);
        return d * paramDouble + (0.0D - 30.0D * d);
    }

    public static class PpmCalibrationInfo {
        public int FidCurrent;
        public float H2Pressure;
        public int Index;
        public boolean IsValid;
        public int Ppm;
    }


}

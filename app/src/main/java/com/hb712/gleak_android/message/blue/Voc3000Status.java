package com.hb712.gleak_android.message.blue;

public class Voc3000Status {
    //出口氢气压力
    public float AirPressure;
    //电量
    public float BatteryVoltage;
    //燃烧室温度
    public float ChamberOuterTemp;
    public byte FIDRange;
    public boolean IsIgnited;
    public boolean IsPumpAOn;
    public boolean IsSolenoidAOn;
    public boolean IsSolenoidBOn;
    public double LongAveragePpm;
    //微电流
    public double PicoAmps;
    public double Ppm;
    public String PpmStr;
    public float PumpPower;
    public double RawPpm;
    public float SamplePressure;
    public double ShortAveragePpm;
    //系统电流
    public float SystemCurrent;
    public float TankPressure;
    //火焰温度
    public float ThermoCouple;
    public String Timestamp;
    public boolean UseAverage;
}

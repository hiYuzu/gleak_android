package com.hb712.gleak_android.message.blue;

public class CurveInfo {
    private String CurveName = "";
    private boolean StdCurve = false;
    private Long _id;

    public CurveInfo() {
    }

    public CurveInfo(Long paramLong, String paramString, boolean paramBoolean) {
        this._id = paramLong;
        this.CurveName = paramString;
        this.StdCurve = paramBoolean;
    }

    public String getCurveName() {
        return this.CurveName;
    }

    public boolean getStdCurve() {
        return this.StdCurve;
    }

    public Long get_id() {
        return this._id;
    }

    public void setCurveName(String paramString) {
        this.CurveName = paramString;
    }

    public void setStdCurve(boolean paramBoolean) {
        this.StdCurve = paramBoolean;
    }

    public void set_id(Long paramLong) {
        this._id = paramLong;
    }
}

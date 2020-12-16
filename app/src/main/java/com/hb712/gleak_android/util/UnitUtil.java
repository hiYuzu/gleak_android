package com.hb712.gleak_android.util;

import com.hb712.gleak_android.entity.FactorCoefficientInfo;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/20 10:28
 */
public class UnitUtil {
    private static final String TAG = UnitUtil.class.getSimpleName();
    private static FactorCoefficientInfo coefficientInfo = null;

    public static void changeFactor(FactorCoefficientInfo paramFactorCoefficientInfo) {
        coefficientInfo = paramFactorCoefficientInfo;
    }

    public static double getMg(double ppm) {
        double d1 = 12.0D;
        try {
            if (coefficientInfo != null) {
                d1 = coefficientInfo.getMoleculeValue();
            }
        } catch (Exception e) {
            LogUtil.errorOut(TAG, e, null);
        }
        return ppm * d1 / 22.4D;
    }
}
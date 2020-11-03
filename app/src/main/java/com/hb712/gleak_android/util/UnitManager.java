package com.hb712.gleak_android.util;

import com.hb712.gleak_android.pojo.FactorCoefficientInfo;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/20 10:28
 */
public class UnitManager {
    private static FactorCoefficientInfo coefficientInfo = null;

    public static void changeFactor(FactorCoefficientInfo paramFactorCoefficientInfo) {
        coefficientInfo = paramFactorCoefficientInfo;
    }

    public static double getMg(double paramDouble) {
        double d1 = 12.0D;
        try {
            if (coefficientInfo != null) {
                d1 = coefficientInfo.getMoleculeValue();
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return paramDouble * d1 / 22.4D;
    }
}
package com.hb712.gleak_android.util;

public class ByteUtil {

    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static int BytesToDword(byte b3, byte b2, byte b1, byte b0) {
        int bb3 = b3;
        int bb2 = b2;
        int bb1 = b1;
        int bb0 = b0;
        if (bb3 < 0) {
            bb3 += 256;
        }
        if (bb2 < 0) {
            bb2 += 256;
        }
        if (bb1 < 0) {
            bb1 += 256;
        }
        if (bb0 < 0) {
            bb0 += 256;
        }
        return (int) (0x00FFFFFFFF & ((bb3 << 24) | (bb2 << 16) | (bb1 << 8) | bb0));
    }

    public static int BytesToWord(byte b1, byte b0) {
        int bb1 = b1;
        int bb0 = b0;
        if (bb1 < 0) {
            bb1 += 256;
        }
        if (bb0 < 0) {
            bb0 += 256;
        }
        return 0xFFFF & ((bb1 << 8) | bb0);
    }

    public static float ConvertKelvinToFahrenheit(float paramFloat) {
        return Math.round((paramFloat - 273.15F) * 1.8F + 32.0F);
    }

    public static String bytesToHex(byte paramByte) {
        paramByte &= 0xFF;
        char[] arrayOfChar = hexArray;
        return new String(new char[]{arrayOfChar[(paramByte >>> 4)], arrayOfChar[(paramByte & 0xF)]});
    }

    public static String bytesToHex(byte[] paramArrayOfByte) {
        char[] arrayOfChar1 = new char[paramArrayOfByte.length * 2];
        int i = 0;
        while (i < paramArrayOfByte.length) {
            int j = paramArrayOfByte[i] & 0xFF;
            char[] arrayOfChar2 = hexArray;
            arrayOfChar1[(i * 2)] = arrayOfChar2[(j >>> 4)];
            arrayOfChar1[(i * 2 + 1)] = arrayOfChar2[(j & 0xF)];
            i += 1;
        }
        return new String(arrayOfChar1);
    }

    public static byte[] getBytes(int paramInt) {
        return new byte[]{(byte) (paramInt & 0xFF), (byte) (paramInt >> 8 & 0xFF), (byte) (paramInt >> 16 & 0xFF), (byte) (paramInt >> 24 & 0xFF)};
    }

    public static float getFloat(byte[] paramArrayOfByte) {
        return Float.intBitsToFloat(getInt(paramArrayOfByte));
    }

    public static int getInt(byte[] paramArrayOfByte) {
        return paramArrayOfByte[0] & 0xFF | paramArrayOfByte[1] << 8 & 0xFF00 | paramArrayOfByte[2] << 16 & 0xFF0000 | paramArrayOfByte[3] << 24 & 0xFF000000;
    }

//    public static long getLong(byte[] paramArrayOfByte) {
//        return paramArrayOfByte[0] & 0xFF | paramArrayOfByte[1] << 8 & 0xFF00 | paramArrayOfByte[2] << 16 & 0xFF0000 | paramArrayOfByte[3] << 24 & 0xFF000000 | paramArrayOfByte[4] << 32 & 0xFF00000000 | paramArrayOfByte[5] << 40 & 0xFF0000000000 | paramArrayOfByte[6] << 48 & 0xFF000000000000 | paramArrayOfByte[7] << 56 & 0xFF00000000000000;
//    }

    public static byte[] strToBytes(String paramString) {
        if ((paramString != null) && (!paramString.trim().equals(""))) {
            byte[] arrayOfByte = new byte[paramString.length() / 2];
            int i = 0;
            while (i < paramString.length() / 2) {
                arrayOfByte[i] = ((byte) Integer.parseInt(paramString.substring(i * 2, i * 2 + 2), 16));
                i += 1;
            }
            return arrayOfByte;
        }
        return new byte[0];
    }

}

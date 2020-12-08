package com.hb712.gleak_android.util;

public class CRC8 {
    static final byte[] CRC8_TAB = {0, 7, 14, 9, 28, 27, 18, 21, 56, 63, 54, 49, 36, 35, 42, 45, 112, 119, 126, 121, 108, 107, 98, 101, 72, 79, 70, 65, 84, 83, 90, 93, -32, -25, -18, -23, -4, -5, -14, -11, -40, -33, -42, -47, -60, -61, -54, -51, -112, -105, -98, -103, -116, -117, -126, -123, -88, -81, -90, -95, -76, -77, -70, -67, -57, -64, -55, -50, -37, -36, -43, -46, -1, -8, -15, -10, -29, -28, -19, -22, -73, -80, -71, -66, -85, -84, -91, -94, -113, -120, -127, -122, -109, -108, -99, -102, 39, 32, 41, 46, 59, 60, 53, 50, 31, 24, 17, 22, 3, 4, 13, 10, 87, 80, 89, 94, 75, 76, 69, 66, 111, 104, 97, 102, 115, 116, 125, 122, -119, -114, -121, -128, -107, -110, -101, -100, -79, -74, -65, -72, -83, -86, -93, -92, -7, -2, -9, -16, -27, -30, -21, -20, -63, -58, -49, -56, -35, -38, -45, -44, 105, 110, 103, 96, 117, 114, 123, 124, 81, 86, 95, 88, 77, 74, 67, 68, 25, 30, 23, 16, 5, 2, 11, 12, 33, 38, 47, 40, 61, 58, 51, 52, 78, 73, 64, 71, 82, 85, 92, 91, 118, 113, 120, 127, 106, 109, 100, 99, 62, 57, 48, 55, 34, 37, 44, 43, 6, 1, 8, 15, 26, 29, 20, 19, -82, -87, -96, -89, -78, -75, -68, -69, -106, -111, -104, -97, -118, -115, -124, -125, -34, -39, -48, -41, -62, -59, -52, -53, -26, -31, -24, -17, -6, -3, -12, -13};
    static byte[] crc8_tab = {0, 94, -68, -30, 97, 63, -35, -125, -62, -100, 126, 32, -93, -3, 31, 65, -99, -61, 33, 127, -4, -94, 64, 30, 95, 1, -29, -67, 62, 96, -126, -36, 35, 125, -97, -63, 66, 28, -2, -96, -31, -65, 93, 3, -128, -34, 60, 98, -66, -32, 2, 92, -33, -127, 99, 61, 124, 34, -64, -98, 29, 67, -95, -1, 70, 24, -6, -92, 39, 121, -101, -59, -124, -38, 56, 102, -27, -69, 89, 7, -37, -123, 103, 57, -70, -28, 6, 88, 25, 71, -91, -5, 120, 38, -60, -102, 101, 59, -39, -121, 4, 90, -72, -26, -89, -7, 27, 69, -58, -104, 122, 36, -8, -90, 68, 26, -103, -57, 37, 123, 58, 100, -122, -40, 91, 5, -25, -71, -116, -46, 48, 110, -19, -77, 81, 15, 78, 16, -14, -84, 47, 113, -109, -51, 17, 79, -83, -13, 112, 46, -52, -110, -45, -115, 111, 49, -78, -20, 14, 80, -81, -15, 19, 77, -50, -112, 114, 44, 109, 51, -47, -113, 12, 82, -80, -18, 50, 108, -114, -48, 83, 13, -17, -79, -16, -82, 76, 18, -111, -49, 45, 115, -54, -108, 118, 40, -85, -11, 23, 73, 8, 86, -76, -22, 105, 55, -43, -117, 87, 9, -21, -75, 54, 104, -118, -44, -107, -53, 41, 119, -12, -86, 72, 22, -23, -73, 85, 11, -120, -42, 52, 106, 43, 117, -105, -55, 74, 20, -10, -88, 116, 42, -56, -106, 21, 75, -87, -9, -74, -24, 10, 84, -41, -119, 107, 53};

    public static String bytesToHexString(byte[] paramArrayOfByte, int paramInt) {
        StringBuilder localStringBuilder = new StringBuilder("");
        if ((paramArrayOfByte != null) && (paramInt > 0)) {
            int i = 0;
            while (i < paramInt) {
                String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
                if (str.length() < 2) {
                    localStringBuilder.append(0);
                }
                localStringBuilder.append(str);
                localStringBuilder.append(" ");
                i += 1;
            }
            return localStringBuilder.toString();
        }
        return null;
    }

    public static byte calcCrc(byte[] paramArrayOfByte, int paramInt) {
        int j = 213;
        int i = 0;
        while (i < paramInt) {
            int k = paramArrayOfByte[i];
            if (k < 0) {
                k += 256;
            }
            j = ((j << 1 | j >> 7) + k) % 256;
            i += 1;
        }
        return (byte) j;
    }

    public static byte calcCrc8(byte[] paramArrayOfByte) {
        return calcCrc8(paramArrayOfByte, 0, paramArrayOfByte.length, (byte) 0);
    }

    public static byte calcCrc8(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        return calcCrc8(paramArrayOfByte, paramInt1, paramInt2, (byte) 0);
    }

    public static byte calcCrc8(byte[] paramArrayOfByte, int paramInt1, int paramInt2, byte paramByte) {
        int i = paramInt1;
        while (i < paramInt1 + paramInt2) {
            paramByte = crc8_tab[((paramArrayOfByte[i] ^ paramByte) & 0xFF)];
            i += 1;
        }
        return paramByte;
    }

    public static byte crc8(byte[] paramArrayOfByte, int paramInt) {
        int j = 0;
        int i = 0;
        while (i < paramInt) {
            int k = paramArrayOfByte[i];
            j = CRC8_TAB[((k ^ j) & 0xFF)];
            i += 1;
        }
        return (byte) (j ^ 0xFFFFFFFF);
    }

    public static void main(String[] paramArrayOfString) {
        int i = calcCrc8(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("");
        localStringBuilder.append(Integer.toHexString(i & 0xFF));
        System.out.println(localStringBuilder.toString());
    }
}

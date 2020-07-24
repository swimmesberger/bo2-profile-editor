package at.swimmesberger.bo2.profile.entity;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ProfileStatsEncoding {
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;
    private static final long BASE_VALUE = 2587251417L;

    public String encode(ProfileStats stats) {
        List<Long> statVals = this.doubleToLong(stats.toDoubleList());
        return this.encodeImpl(statVals);
    }

    public ProfileStats decode(String encodedString) {
        List<Long> statVals = this.decodeImpl(encodedString);
        List<Double> doubleStatVals = this.longToDouble(statVals);
        return ProfileStats.fromDoubleValues(doubleStatVals);
    }

    private String encodeImpl(List<Long> values) {
        long num1 = 0;
        int num2 = 0;
        StringBuilder str = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            long val = values.get(index);
            int realValSigned = this.decodeLongSigned(val);
            long num3 = realValSigned ^ BASE_VALUE;
            if (num2 > 0) {
                long num4 = (((int) num1 | (int) num3 << num2) & 31);
                str.append(ALPHABET.charAt((int) num4));
                num2 = 5 - num2;
            }
            for (; num2 < 28; num2 += 5) {
                long num4 = num3 >> num2 & 31;
                str.append(ALPHABET.charAt((int) num4));
            }
            num1 = num3 >> num2;
            num2 = 32 - num2;
        }
        if (num2 > 0)
            str.append(ALPHABET.charAt((int) num1));
        return str.toString();
    }

    private long decodeLong(long val) {
        return Integer.toUnsignedLong(this.decodeLongSigned(val));
    }

    private int decodeLongSigned(long val) {
        int signedVal = (int) val;
        byte[] bytes = intToByteArray(signedVal);
        reverseByteArray(bytes);
        return byteArrayToInt(bytes);
    }

    private List<Long> decodeImpl(String encodedString) {
        List<Long> uintList = new ArrayList<>();
        long num1 = BASE_VALUE;
        int num2 = 0;
        for (int index = 0; index < encodedString.length(); index++) {
            int num3 = ALPHABET.indexOf(encodedString.charAt(index));
            num1 ^= num3 << num2;
            // discard bits
            num1 = Integer.toUnsignedLong((int) num1);
            num2 += 5;
            if (num2 > 31) {
                byte[] bytes = intToByteArray((int) num1);
                reverseByteArray(bytes);
                int int32 = byteArrayToInt(bytes);
                long uint32 = Integer.toUnsignedLong(int32);
                uintList.add(uint32);
                num2 &= 7;
                num1 = BASE_VALUE ^ num3 >> 5 - num2;
            }
        }
        return uintList;
    }

    private List<Double> longToDouble(List<Long> longValues) {
        List<Double> doubleStatVals = new ArrayList<>(longValues.size());
        for (long val : longValues) {
            long realVal = this.decodeLong(val);
            double unroundedVal = Math.pow((double) realVal, ProfileStats.PROFILE_STEP_VALUE);
            double roundedVal = BigDecimal.valueOf(unroundedVal).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue();
            if (roundedVal > ProfileStats.MAXIMUM_STAT_VALUE) {
                roundedVal = ProfileStats.MAXIMUM_STAT_VALUE;
            }
            doubleStatVals.add(roundedVal);
        }
        return doubleStatVals;
    }

    private List<Long> doubleToLong(List<Double> doubleValues) {
        List<Long> longStatVals = new ArrayList<>(doubleValues.size());
        for (double d : doubleValues) {
            long unsignedValue = Math.round(Math.pow(d, 4.0 / 3.0));
            longStatVals.add(this.decodeLong(unsignedValue));
        }
        return longStatVals;
    }

    private final int byteArrayToInt(byte[] data) {
        return byteArrayToInt(data, BYTE_ORDER);
    }

    private final int byteArrayToInt(byte[] data, ByteOrder endian) {
        return byteArrayToInt(data, 0, endian);
    }

    private final int byteArrayToInt(byte[] data, int startIndex, ByteOrder endian) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(endian);
        return bb.getInt(startIndex);
    }

    private final long byteArrayToLong(byte[] data) {
        return byteArrayToLong(data, BYTE_ORDER);
    }

    private final long byteArrayToLong(byte[] data, ByteOrder endian) {
        return byteArrayToLong(data, 0, endian);
    }

    private final long byteArrayToLong(byte[] data, int startIndex, ByteOrder endian) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(endian);
        return bb.getLong(startIndex);
    }


    private final byte[] intToByteArray(int value) {
        return intToByteArray(value, BYTE_ORDER);
    }

    private final byte[] intToByteArray(int value, ByteOrder endian) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(endian);
        bb.putInt(value);
        return bb.array();
    }

    private final byte[] longToByteArray(long value) {
        return longToByteArray(value, BYTE_ORDER);
    }

    private final byte[] longToByteArray(long value, ByteOrder endian) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(endian);
        bb.putLong(value);
        return bb.array();
    }

    private void reverseByteArray(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
}

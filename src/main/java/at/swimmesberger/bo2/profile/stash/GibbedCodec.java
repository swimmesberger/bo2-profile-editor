package at.swimmesberger.bo2.profile.stash;

import java.util.Arrays;
import java.util.Base64;
import java.util.zip.CRC32;

/**
 * Encodes and decodes BL2 Gibbed item codes.
 *
 * Two wire formats are recognised on decode:
 *
 *  1. Gibbed-encrypted (used by our encoder and the original Gibbed SaveEdit):
 *       "BL2(" + base64(seed[4-LE] + lcg_xor(serial, seed)) + ")"
 *     The 4-byte LE seed precedes the LCG-XOR'd serial.
 *     LCG: s = (s * 0x10A860C1 + 0x5FFFAC8D) mod 2^32, XOR key = s >> 24.
 *
 *  2. Raw format (used by lootlemon.com and similar databases):
 *       "BL2(" + base64(serial) + ")"
 *     The serial bytes are stored directly without any seed or XOR layer.
 *
 * Detection: BL2 item serials store the version (7) in the lowest 3 bits of
 * the first byte. Shields/grenades/etc. have first byte 0x07; weapons pack
 * extra fields into the upper 5 bits (e.g. 0x87). We detect format by checking
 * (byte & 0x07) == 0x07: try Gibbed-decrypted result first, then raw bytes.
 */
public class GibbedCodec {
    private static final String PREFIX = "BL2(";
    private static final long LCG_MULT = 0x10A860C1L;
    private static final long LCG_INC  = 0x5FFFAC8DL;

    public String encode(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Cannot encode empty item data");
        }
        int seed = crc32Seed(data);
        byte[] encrypted = lcgXor(data, seed);
        byte[] combined = new byte[4 + encrypted.length];
        // little-endian seed bytes, matching Gibbed's C# BitConverter
        combined[0] = (byte)  seed;
        combined[1] = (byte) (seed >>> 8);
        combined[2] = (byte) (seed >>> 16);
        combined[3] = (byte) (seed >>> 24);
        System.arraycopy(encrypted, 0, combined, 4, encrypted.length);
        return PREFIX + Base64.getEncoder().encodeToString(combined) + ")";
    }

    public byte[] decode(String code) {
        if (!isValid(code)) {
            throw new IllegalArgumentException("Not a valid BL2 Gibbed code: " + code);
        }
        String b64 = code.substring(PREFIX.length(), code.length() - 1);
        byte[] combined;
        try {
            combined = Base64.getDecoder().decode(b64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gibbed code contains invalid Base64: " + code, e);
        }
        if (combined.length < 1) {
            throw new IllegalArgumentException("Gibbed code data too short: " + code);
        }
        // Standard Gibbed format: 4-byte LE seed + LCG-XOR'd serial
        if (combined.length >= 4) {
            int seed =  (combined[0] & 0xFF)
                     | ((combined[1] & 0xFF) << 8)
                     | ((combined[2] & 0xFF) << 16)
                     | ((combined[3] & 0xFF) << 24);
            byte[] decrypted = lcgXor(Arrays.copyOfRange(combined, 4, combined.length), seed);
            // BL2 version (7) lives in the low 3 bits of the first serial byte
            if (decrypted.length > 0 && (decrypted[0] & 0x07) == 0x07) {
                return decrypted;
            }
        }
        // Raw format: serial stored without encryption; version still in low 3 bits
        if ((combined[0] & 0x07) == 0x07) {
            return combined;
        }
        // Neither interpretation yields a valid BL2 serial; return decrypted for legacy behaviour
        if (combined.length < 4) {
            throw new IllegalArgumentException("Gibbed code data too short: " + code);
        }
        int seed =  (combined[0] & 0xFF)
                 | ((combined[1] & 0xFF) << 8)
                 | ((combined[2] & 0xFF) << 16)
                 | ((combined[3] & 0xFF) << 24);
        return lcgXor(Arrays.copyOfRange(combined, 4, combined.length), seed);
    }

    public boolean isValid(String code) {
        return code != null && code.startsWith(PREFIX) && code.endsWith(")");
    }

    private byte[] lcgXor(byte[] data, int seed) {
        byte[] result = new byte[data.length];
        long s = Integer.toUnsignedLong(seed);
        for (int i = 0; i < data.length; i++) {
            s = (s * LCG_MULT + LCG_INC) & 0xFFFFFFFFL;
            result[i] = (byte) (data[i] ^ (s >>> 24));
        }
        return result;
    }

    private int crc32Seed(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return (int) crc.getValue();
    }
}

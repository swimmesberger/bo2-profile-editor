package at.swimmesberger.bo2.profile.stash;

import java.util.Base64;

/**
 * Encodes and decodes BL2 Gibbed item codes.
 *
 * A BL2 code is "BL2(" + base64(serialBytes) + ")" where serialBytes are the
 * raw bytes the game stores in a stash slot — no additional encryption layer.
 * De-obfuscation of the inner bit-packed fields is separate and not needed here.
 */
public class GibbedCodec {
    private static final String PREFIX = "BL2(";

    public String encode(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Cannot encode empty item data");
        }
        return PREFIX + Base64.getEncoder().encodeToString(data) + ")";
    }

    public byte[] decode(String code) {
        if (!isValid(code)) {
            throw new IllegalArgumentException("Not a valid BL2 Gibbed code: " + code);
        }
        String b64 = code.substring(PREFIX.length(), code.length() - 1);
        byte[] serial;
        try {
            serial = Base64.getDecoder().decode(b64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gibbed code contains invalid Base64: " + code, e);
        }
        if (serial.length < 5 || serial.length > 40) {
            throw new IllegalArgumentException(
                    "BL2 serial length " + serial.length + " outside valid range [5, 40]: " + code);
        }
        if ((serial[0] & 0x07) != 0x07) {
            throw new IllegalArgumentException(
                    "Not a BL2 serial — version bits in first byte must be 7: " + code);
        }
        return serial;
    }

    public boolean isValid(String code) {
        return code != null && code.startsWith(PREFIX) && code.endsWith(")");
    }
}

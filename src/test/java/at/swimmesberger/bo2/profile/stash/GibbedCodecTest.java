package at.swimmesberger.bo2.profile.stash;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class GibbedCodecTest {

    // Real lootlemon codes — the canonical BL2-code format: BL2( + base64(rawSerial) + )
    // Shield/grenade/relic/COM: first byte = 0x07 (version 7, upper bits zero)
    private static final String LOOTLEMON_SHAM    = "BL2(BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=)";
    // Weapon: first byte = 0x87 (version 7 in low 3 bits; upper 5 bits carry weapon-class data)
    private static final String LOOTLEMON_TATTLER = "BL2(hwAAAABpwgFA6AAAA6HVxmIhwVIBEAELC5YHLBJY/v+7AYTBMgHw)";

    // Raw serial bytes (pure base64-decode of the inner payload)
    private static final byte[] SHAM_BYTES    = Base64.getDecoder().decode("BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=");
    private static final byte[] TATTLER_BYTES = Base64.getDecoder().decode("hwAAAABpwgFA6AAAA6HVxmIhwVIBEAELC5YHLBJY/v+7AYTBMgHw");

    // ── isValid ───────────────────────────────────────────────────────────────

    @Test
    public void isValid_validCode_returnsTrue() {
        assertTrue(new GibbedCodec().isValid(LOOTLEMON_SHAM));
    }

    @Test
    public void isValid_nullInput_returnsFalse() {
        assertFalse(new GibbedCodec().isValid(null));
    }

    @Test
    public void isValid_emptyString_returnsFalse() {
        assertFalse(new GibbedCodec().isValid(""));
    }

    @Test
    public void isValid_missingPrefix_returnsFalse() {
        assertFalse(new GibbedCodec().isValid("BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=)"));
    }

    @Test
    public void isValid_missingSuffix_returnsFalse() {
        assertFalse(new GibbedCodec().isValid("BL2(BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ="));
    }

    @Test
    public void isValid_wrongGame_returnsFalse() {
        assertFalse(new GibbedCodec().isValid("TPS(BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=)"));
    }

    // ── encode ────────────────────────────────────────────────────────────────

    @Test
    public void encode_alwaysStartsWithBl2Prefix() {
        assertTrue(new GibbedCodec().encode(SHAM_BYTES).startsWith("BL2("));
    }

    @Test
    public void encode_alwaysEndsWithClosingParen() {
        assertTrue(new GibbedCodec().encode(SHAM_BYTES).endsWith(")"));
    }

    @Test
    public void encode_knownInput_returnsKnownCode() {
        assertEquals(LOOTLEMON_SHAM, new GibbedCodec().encode(SHAM_BYTES));
    }

    @Test
    public void encode_sameInputTwice_producesSameCode() {
        GibbedCodec codec = new GibbedCodec();
        assertEquals(codec.encode(SHAM_BYTES), codec.encode(SHAM_BYTES));
    }

    @Test
    public void encode_differentInputs_produceDifferentCodes() {
        GibbedCodec codec = new GibbedCodec();
        assertNotEquals(codec.encode(SHAM_BYTES), codec.encode(TATTLER_BYTES));
    }

    @Test
    public void encode_emptyData_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new GibbedCodec().encode(new byte[]{}));
    }

    @Test
    public void encode_nullData_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GibbedCodec().encode(null));
    }

    // ── decode ────────────────────────────────────────────────────────────────

    @Test
    public void decode_knownCode_returnsKnownBytes() {
        assertArrayEquals(SHAM_BYTES, new GibbedCodec().decode(LOOTLEMON_SHAM));
    }

    @Test
    public void decode_rawFormat_shield_versionBitsCorrect() {
        byte[] serial = new GibbedCodec().decode(LOOTLEMON_SHAM);
        assertEquals(7, serial[0] & 0x07, "Low 3 bits of first byte must be 7 (BL2 serial version)");
    }

    @Test
    public void decode_rawFormat_shield_returnsAllBytes() {
        assertEquals(38, new GibbedCodec().decode(LOOTLEMON_SHAM).length);
    }

    @Test
    public void decode_rawFormat_weapon_versionBitsCorrect() {
        byte[] serial = new GibbedCodec().decode(LOOTLEMON_TATTLER);
        assertEquals(7, serial[0] & 0x07, "Low 3 bits of first byte must be 7 (BL2 serial version)");
        assertEquals(0x87, serial[0] & 0xFF, "Weapon first byte should be 0x87");
    }

    @Test
    public void decode_rawFormat_weapon_returnsAllBytes() {
        assertEquals(39, new GibbedCodec().decode(LOOTLEMON_TATTLER).length);
    }

    @Test
    public void decode_invalidPrefix_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GibbedCodec().decode("TPS(BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=)"));
    }

    @Test
    public void decode_invalidBase64Content_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GibbedCodec().decode("BL2(not!valid!base64)"));
    }

    @Test
    public void decode_payloadTooShort_throwsIllegalArgumentException() {
        // 4 bytes — one short of the 5-byte minimum BL2 serial
        String tooShort = "BL2(" + Base64.getEncoder().encodeToString(new byte[]{0x07, 0x00, 0x00, 0x00}) + ")";
        assertThrows(IllegalArgumentException.class, () -> new GibbedCodec().decode(tooShort));
    }

    @Test
    public void decode_payloadTooLong_throwsIllegalArgumentException() {
        // 41 bytes — one over Gibbed's maximum of 40
        byte[] tooLong = new byte[41];
        tooLong[0] = 0x07;
        String code = "BL2(" + Base64.getEncoder().encodeToString(tooLong) + ")";
        assertThrows(IllegalArgumentException.class, () -> new GibbedCodec().decode(code));
    }

    @Test
    public void decode_invalidVersionBits_throwsIllegalArgumentException() {
        // version bits = 1 (not 7) in the first byte
        byte[] invalid = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00};
        String code = "BL2(" + Base64.getEncoder().encodeToString(invalid) + ")";
        assertThrows(IllegalArgumentException.class, () -> new GibbedCodec().decode(code));
    }

    // ── round-trip ────────────────────────────────────────────────────────────

    @Test
    public void roundTrip_shield_bytesToCode() {
        GibbedCodec codec = new GibbedCodec();
        assertArrayEquals(SHAM_BYTES, codec.decode(codec.encode(SHAM_BYTES)));
    }

    @Test
    public void roundTrip_weapon_bytesToCode() {
        GibbedCodec codec = new GibbedCodec();
        assertArrayEquals(TATTLER_BYTES, codec.decode(codec.encode(TATTLER_BYTES)));
    }

    @Test
    public void roundTrip_shield_codeToBytes() {
        GibbedCodec codec = new GibbedCodec();
        assertEquals(LOOTLEMON_SHAM, codec.encode(codec.decode(LOOTLEMON_SHAM)));
    }

    @Test
    public void roundTrip_weapon_codeToBytes() {
        GibbedCodec codec = new GibbedCodec();
        assertEquals(LOOTLEMON_TATTLER, codec.encode(codec.decode(LOOTLEMON_TATTLER)));
    }
}

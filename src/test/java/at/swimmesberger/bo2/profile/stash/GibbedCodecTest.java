package at.swimmesberger.bo2.profile.stash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GibbedCodecTest {

    private static final byte[] ITEM_16 =
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    // Precomputed: GibbedCodec.encode(ITEM_16) — deterministic because seed = CRC32(data)
    private static final String CODE_16 = "BL2(8YBMCcAU8/oUO7dfO3J7H22NpLw=)";

    // ── isValid ───────────────────────────────────────────────────────────────

    @Test
    public void isValid_validCode_returnsTrue() {
        assertTrue(new GibbedCodec().isValid(CODE_16));
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
        assertFalse(new GibbedCodec().isValid("CUyA8cAU8/oUO7dfO3J7H22NpLw=)"));
    }

    @Test
    public void isValid_missingSuffix_returnsFalse() {
        assertFalse(new GibbedCodec().isValid("BL2(CUyA8cAU8/oUO7dfO3J7H22NpLw="));
    }

    @Test
    public void isValid_wrongGame_returnsFalse() {
        assertFalse(new GibbedCodec().isValid("TPS(CUyA8cAU8/oUO7dfO3J7H22NpLw=)"));
    }

    // ── encode ────────────────────────────────────────────────────────────────

    @Test
    public void encode_alwaysStartsWithBl2Prefix() {
        String code = new GibbedCodec().encode(new byte[]{42, 43});
        assertTrue(code.startsWith("BL2("), "Expected BL2( prefix, got: " + code);
    }

    @Test
    public void encode_alwaysEndsWithClosingParen() {
        String code = new GibbedCodec().encode(new byte[]{42, 43});
        assertTrue(code.endsWith(")"), "Expected ) suffix, got: " + code);
    }

    @Test
    public void encode_knownInput_returnsKnownCode() {
        assertEquals(CODE_16, new GibbedCodec().encode(ITEM_16));
    }

    @Test
    public void encode_sameInputTwice_producesSameCode() {
        GibbedCodec codec = new GibbedCodec();
        assertEquals(codec.encode(ITEM_16), codec.encode(ITEM_16));
    }

    @Test
    public void encode_differentInputs_produceDifferentCodes() {
        GibbedCodec codec = new GibbedCodec();
        assertNotEquals(codec.encode(new byte[]{1}), codec.encode(new byte[]{2}));
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

    // Lootlemon raw format — shields/grenades/relics/COMs: first byte = 0x07 (version 7, upper bits 0)
    private static final String LOOTLEMON_SHAM    = "BL2(BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=)";
    // Lootlemon raw format — weapon: first byte = 0x87 (version 7 in low 3 bits; upper 5 bits carry weapon-class data)
    private static final String LOOTLEMON_TATTLER = "BL2(hwAAAABpwgFA6AAAA6HVxmIhwVIBEAELC5YHLBJY/v+7AYTBMgHw)";

    @Test
    public void decode_knownCode_returnsKnownBytes() {
        assertArrayEquals(ITEM_16, new GibbedCodec().decode(CODE_16));
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
        // Weapon first byte = 0x87: upper 5 bits carry weapon fields; low 3 bits = version 7
        byte[] serial = new GibbedCodec().decode(LOOTLEMON_TATTLER);
        assertEquals(7, serial[0] & 0x07, "Low 3 bits of first byte must be 7 (BL2 serial version)");
        assertEquals(0x87, serial[0] & 0xFF, "Weapon first byte should be 0x87");
    }

    @Test
    public void decode_rawFormat_weapon_returnsAllBytes() {
        assertEquals(39, new GibbedCodec().decode(LOOTLEMON_TATTLER).length);
    }

    @Test
    public void decode_singleByte_roundTrips() {
        GibbedCodec codec = new GibbedCodec();
        byte[] data = {(byte) 0xFF};
        assertArrayEquals(data, codec.decode(codec.encode(data)));
    }

    @Test
    public void decode_invalidPrefix_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GibbedCodec().decode("TPS(CUyA8cAU8/oUO7dfO3J7H22NpLw=)"));
    }

    @Test
    public void decode_invalidBase64Content_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GibbedCodec().decode("BL2(not!valid!base64)"));
    }

    @Test
    public void decode_payloadTooShort_throwsIllegalArgumentException() {
        // Base64 of only 3 bytes — fewer than the 4-byte seed header
        String tooShort = "BL2(" + java.util.Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}) + ")";
        assertThrows(IllegalArgumentException.class, () -> new GibbedCodec().decode(tooShort));
    }

    // ── round-trip ────────────────────────────────────────────────────────────

    @Test
    public void roundTrip_smallPayload() {
        GibbedCodec codec = new GibbedCodec();
        byte[] data = {10, 20, 30, 40};
        assertArrayEquals(data, codec.decode(codec.encode(data)));
    }

    @Test
    public void roundTrip_largePayload() {
        GibbedCodec codec = new GibbedCodec();
        byte[] data = new byte[256];
        for (int i = 0; i < data.length; i++) data[i] = (byte) i;
        assertArrayEquals(data, codec.decode(codec.encode(data)));
    }

    @Test
    public void roundTrip_allZeroBytes() {
        GibbedCodec codec = new GibbedCodec();
        byte[] data = new byte[8];
        assertArrayEquals(data, codec.decode(codec.encode(data)));
    }

    @Test
    public void roundTrip_allMaxBytes() {
        GibbedCodec codec = new GibbedCodec();
        byte[] data = new byte[8];
        java.util.Arrays.fill(data, (byte) 0xFF);
        assertArrayEquals(data, codec.decode(codec.encode(data)));
    }
}

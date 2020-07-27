package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoldenKeysEncodingTest {
    private static final byte[] LARGE_ENCODED = new byte[]{0, (byte)255, 0, 1, (byte)255, 0, 2, (byte)255, 0, 3, 35, 0};
    @Test
    public void testDecode() {
        GoldenKeysEncoding encoding = new GoldenKeysEncoding();
        int goldenKeys = encoding.decode(TestFixtures.getEncodedGoldenKeysProfile1());
        assertEquals(52, goldenKeys);
    }

    @Test
    public void testDecodeLarge() {
        GoldenKeysEncoding encoding = new GoldenKeysEncoding();
        int goldenKeys = encoding.decode(LARGE_ENCODED);
        assertEquals(800, goldenKeys);
    }

    @Test
    public void testEncodeSmall() {
        GoldenKeysEncoding encoding = new GoldenKeysEncoding();
        byte[] encodedGoldenKeys = encoding.encode(52);
        assertArrayEquals(new byte[]{0, 52, 0}, encodedGoldenKeys);
    }

    @Test
    public void testEncodeLarge() {
        GoldenKeysEncoding encoding = new GoldenKeysEncoding();
        byte[] encodedGoldenKeys = encoding.encode(800);
        assertArrayEquals(LARGE_ENCODED, encodedGoldenKeys);
    }
}

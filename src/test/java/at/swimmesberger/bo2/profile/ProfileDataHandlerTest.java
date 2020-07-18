package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.util.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ProfileDataHandlerTest {
    @Test
    public void testDecompress() throws IOException {
        ProfileDataHandler profileDataHandler = new ProfileDataHandler();
        byte[] uncompressed;
        try(InputStream in = ProfileDataHandlerTest.class.getResourceAsStream("profile1.bin")) {
            uncompressed = profileDataHandler.decompressInMemory(in);
        }
        byte[] compUncompressed;
        try(InputStream in = ProfileDataHandler.class.getResourceAsStream("profile1.bin.uncompressed")) {
            compUncompressed = IOUtils.toByteArray(in);
        }
        assertArrayEquals(compUncompressed, uncompressed);
    }

    @Test
    public void testCompress() throws IOException {
        ProfileDataHandler profileDataHandler = new ProfileDataHandler();
        byte[] compressed;
        try(InputStream in = ProfileDataHandlerTest.class.getResourceAsStream("profile1.bin.uncompressed")) {
            compressed = profileDataHandler.compressInMemory(in);
        }
        byte[] compCompressed;
        try(InputStream in = ProfileDataHandler.class.getResourceAsStream("profile1.bin")) {
            compCompressed = IOUtils.toByteArray(in);
        }
        assertArrayEquals(compCompressed, compressed);
    }
}

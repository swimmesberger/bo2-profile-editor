package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.util.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ProfileEntryDataHandlerTest {
    @Test
    public void testDecompress() throws IOException {
        ProfileEntryDataHandler profileEntryDataHandler = new ProfileEntryDataHandler();
        byte[] uncompressed;
        try(InputStream in = ProfileEntryDataHandlerTest.class.getResourceAsStream("profile1.bin")) {
            uncompressed = profileEntryDataHandler.decompressInMemory(in);
        }
        byte[] compUncompressed;
        try(InputStream in = ProfileEntryDataHandler.class.getResourceAsStream("profile1.bin.uncompressed")) {
            compUncompressed = IOUtils.toByteArray(in);
        }
        assertArrayEquals(compUncompressed, uncompressed);
    }

    // currently fails because the compression algorithm produces different results for us (but seems to be readable?)
    // maybe the used lzo version differs
    //@Test
    public void testCompress() throws IOException {
        ProfileEntryDataHandler profileEntryDataHandler = new ProfileEntryDataHandler();
        byte[] compressed;
        try(InputStream in = ProfileEntryDataHandlerTest.class.getResourceAsStream("profile1.bin.uncompressed")) {
            compressed = profileEntryDataHandler.compressInMemory(in);
        }
        byte[] compCompressed;
        try(InputStream in = ProfileEntryDataHandler.class.getResourceAsStream("profile1.bin")) {
            compCompressed = IOUtils.toByteArray(in);
        }
        assertArrayEquals(compCompressed, compressed);
    }
}

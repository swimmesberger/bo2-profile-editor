package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.util.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryProfileEntryWriterTest {
    @Test
    public void testProfile1() throws IOException {
        byte[] writtenData;
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); BinaryProfileEntryWriter writer = new BinaryProfileEntryWriter(bout)) {
            writer.write(ProfileEntryFixtures.createProfile1Entries());
            writtenData = bout.toByteArray();
        }
        assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(writtenData), BinaryProfileEntryWriterTest.class.getResourceAsStream("profile1.bin.uncompressed")));
    }
}

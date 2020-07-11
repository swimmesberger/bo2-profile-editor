package at.swimmesberger.bo2.profile;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinaryProfileEntryReaderTest {
    @Test
    public void testReadProfile1() throws IOException {
        try (InputStream in = BinaryProfileEntryReaderTest.class.getResourceAsStream("profile1.bin.uncompressed")) {
            BinaryProfileEntryReader reader = new BinaryProfileEntryReader(in);
            List<ProfileEntry<?>> entries = reader.readEntries();
            List<ProfileEntry<?>> expectedEntries = ProfileEntryFixtures.createProfile1Entries();

            assertEquals(expectedEntries.size(), entries.size());

            for(int i = 0; i<expectedEntries.size(); i++) {
                ProfileEntry<?> readEntry = entries.get(i);
                ProfileEntry<?> expectedEntry = expectedEntries.get(i);
                assertEquals(readEntry, expectedEntry, "At index " + i);
            }
        }
    }
}

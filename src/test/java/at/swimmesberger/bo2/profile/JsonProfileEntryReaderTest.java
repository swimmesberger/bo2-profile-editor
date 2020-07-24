package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.conversion.JsonProfileEntryReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonProfileEntryReaderTest {
    @Test
    public void testReadProfile1() throws IOException {
        try (InputStream in = BinaryProfileEntryReaderTest.class.getResourceAsStream("profile1.json")) {
            JsonProfileEntryReader reader = new JsonProfileEntryReader(in);
            List<ProfileEntry<?>> entries = reader.readEntries();
            List<ProfileEntry<?>> expectedEntries = TestFixtures.createProfile1Entries();

            assertEquals(expectedEntries.size(), entries.size());

            for(int i = 0; i<expectedEntries.size(); i++) {
                ProfileEntry<?> readEntry = entries.get(i);
                ProfileEntry<?> expectedEntry = expectedEntries.get(i);
                assertEquals(readEntry, expectedEntry, "At index " + i);
            }
        }
    }
}

package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.BinaryProfileEntryReaderTest;
import at.swimmesberger.bo2.profile.TestFixtures;
import at.swimmesberger.bo2.profile.entity.conversion.JsonProfileDataReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonProfileDataReaderTest {
    @Test
    public void testRead() throws IOException {
        try (InputStream in = BinaryProfileEntryReaderTest.class.getResourceAsStream("profile_data1.json")) {
            JsonProfileDataReader dataReader = new JsonProfileDataReader(in);
            ProfileData profileData = dataReader.readData();
            assertEquals(TestFixtures.createProfile1Data(), profileData);
        }
    }
}

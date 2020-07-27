package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileDataConverterTest {
    @Test
    public void testConvert() {
        ProfileDataConverter converter = new ProfileDataConverter();
        ProfileData data = converter.decodeEntries(ProfileEntries.from(TestFixtures.createProfile1Entries()));
        assertEquals(new ProfileData(52, TestFixtures.createProfile1EntriesStats(), 1315, 10, TestFixtures.createMixedCustomizations()), data);
    }
}

package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileDataConverterTest {
    @Test
    public void testConvert() {
        ProfileDataConverter converter = new ProfileDataConverter();
        ProfileData data = converter.convert(ProfileEntries.from(TestFixtures.createProfile1Entries()));
        assertEquals(new ProfileData(52, TestFixtures.createMixedStats(), 1315, 10, TestFixtures.createMixedCustomizations()), data);
    }
}

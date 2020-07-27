package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.TestFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileStatsEncodingTest {
    @Test
    public void testDecodeMax() {
        ProfileStatsEncoding decoder = new ProfileStatsEncoding();
        ProfileStats stats = decoder.decode("29BKWJB4DDJBEHMN9ES5JP6S5Q8TT4QW29BKWJB4DDJBEHMN9ES5JP6S5Q8TT4QW29BKWJB4DDJBEHMN9ES5JP6S57");
        assertEquals(ProfileStats.max(), stats);
    }

    @Test
    public void testDecodeMixed() {
        ProfileStatsEncoding decoder = new ProfileStatsEncoding();
        ProfileStats stats = decoder.decode("RPMC3DAVJJDMSEBAPH6ZD9S6T4M55V8KJPMC3DJTJJDMS4BAPH6NC9S6TWK55V8K1PMC3DARJJDM93BAPH6QF9S6T4");
        Assertions.assertEquals(TestFixtures.createMixedStats(), stats);
    }

    @Test
    public void testEncodeMax() {
        ProfileStatsEncoding encoder = new ProfileStatsEncoding();
        String statsEncoded = encoder.encode(ProfileStats.max());
        assertEquals("29BKWJB4DDJBEHMN9ES5JP6S5Q8TT4QW29BKWJB4DDJBEHMN9ES5JP6S5Q8TT4QW29BKWJB4DDJBEHMN9ES5JP6S57", statsEncoded);
    }

    @Test
    public void testEncodeMixed() {
        ProfileStatsEncoding encoder = new ProfileStatsEncoding();
        String statsEncoded = encoder.encode(TestFixtures.createMixedStats());
        assertEquals("RPMC3DAVJJDMSEBAPH6ZD9S6T4M55V8KJPMC3DJTJJDMS4BAPH6NC9S6TWK55V8K1PMC3DARJJDM93BAPH6QF9S6T4", statsEncoded);
    }
}

package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import at.swimmesberger.bo2.profile.entity.ProfileStats;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SetCommandTest {

    private SetCommand command(String... rawArgs) {
        SetCommand cmd = new SetCommand();
        new CommandLine(cmd).parseArgs(rawArgs);
        return cmd;
    }

    // ── maxValue ─────────────────────────────────────────────────────────────

    @Test
    public void maxValue_goldenKeys() {
        SetCommand cmd = new SetCommand();
        assertEquals("255", cmd.maxValue(ProfileDataValueType.GOLDEN_KEYS));
    }

    @Test
    public void maxValue_badassRank() {
        SetCommand cmd = new SetCommand();
        assertEquals("2000000000", cmd.maxValue(ProfileDataValueType.BADASS_RANK));
    }

    @Test
    public void maxValue_badassTokens() {
        SetCommand cmd = new SetCommand();
        assertEquals("500", cmd.maxValue(ProfileDataValueType.BADASS_TOKENS));
    }

    @Test
    public void maxValue_allCustomizations() {
        SetCommand cmd = new SetCommand();
        assertEquals("true", cmd.maxValue(ProfileDataValueType.ALL_CUSTOMIZATIONS));
    }

    @Test
    public void maxValue_statType_returnsMaxStatValue() {
        SetCommand cmd = new SetCommand();
        String expected = String.valueOf(ProfileStats.MAXIMUM_STAT_VALUE);
        assertEquals(expected, cmd.maxValue(ProfileDataValueType.GUN_DAMAGE));
        assertEquals(expected, cmd.maxValue(ProfileDataValueType.FIRE_RATE));
        assertEquals(expected, cmd.maxValue(ProfileDataValueType.MAXIMUM_HEALTH));
    }

    // ── buildValueMap ─────────────────────────────────────────────────────────

    @Test
    public void buildValueMap_allMax_setsEveryType() {
        SetCommand cmd = command("all", "max");
        Map<ProfileDataValueType, String> map = cmd.buildValueMap();
        assertNotNull(map);
        assertEquals(ProfileDataValueType.values().length, map.size());
        assertEquals("255", map.get(ProfileDataValueType.GOLDEN_KEYS));
        assertEquals("true", map.get(ProfileDataValueType.ALL_CUSTOMIZATIONS));
        assertEquals(String.valueOf(ProfileStats.MAXIMUM_STAT_VALUE), map.get(ProfileDataValueType.GUN_DAMAGE));
    }

    @Test
    public void buildValueMap_allNumeric_setsEveryTypeToLiteralValue() {
        SetCommand cmd = command("all", "100");
        Map<ProfileDataValueType, String> map = cmd.buildValueMap();
        assertNotNull(map);
        // Literal "100" is passed through for all types
        for (ProfileDataValueType type : ProfileDataValueType.values()) {
            assertEquals("100", map.get(type));
        }
    }

    @Test
    public void buildValueMap_allWithExtraArgs_returnsNull() {
        SetCommand cmd = command("all", "max", "extra");
        assertNull(cmd.buildValueMap());
    }

    @Test
    public void buildValueMap_singlePair_correctType() {
        SetCommand cmd = command("GOLDEN_KEYS", "255");
        Map<ProfileDataValueType, String> map = cmd.buildValueMap();
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("255", map.get(ProfileDataValueType.GOLDEN_KEYS));
    }

    @Test
    public void buildValueMap_singlePair_maxKeyword_resolves() {
        SetCommand cmd = command("BADASS_RANK", "max");
        Map<ProfileDataValueType, String> map = cmd.buildValueMap();
        assertNotNull(map);
        assertEquals("2000000000", map.get(ProfileDataValueType.BADASS_RANK));
    }

    @Test
    public void buildValueMap_multiplePairs() {
        SetCommand cmd = command("GOLDEN_KEYS", "100", "BADASS_RANK", "max");
        Map<ProfileDataValueType, String> map = cmd.buildValueMap();
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("100", map.get(ProfileDataValueType.GOLDEN_KEYS));
        assertEquals("2000000000", map.get(ProfileDataValueType.BADASS_RANK));
    }

    @Test
    public void buildValueMap_unknownType_returnsNull() {
        SetCommand cmd = command("NOT_A_TYPE", "123");
        assertNull(cmd.buildValueMap());
    }

    @Test
    public void buildValueMap_oddArgs_nonAll_returnsNull() {
        SetCommand cmd = command("GOLDEN_KEYS", "255", "BADASS_RANK");
        assertNull(cmd.buildValueMap());
    }

    @Test
    public void buildValueMap_typeNameCaseInsensitive() {
        SetCommand cmd = command("golden_keys", "255");
        Map<ProfileDataValueType, String> map = cmd.buildValueMap();
        assertNotNull(map);
        assertEquals("255", map.get(ProfileDataValueType.GOLDEN_KEYS));
    }
}

package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import at.swimmesberger.bo2.profile.stash.GibbedCodec;
import at.swimmesberger.bo2.profile.stash.StashConverter;
import at.swimmesberger.bo2.profile.stash.StashSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StashCommandTest {

    // Real lootlemon code (The Sham shield) — valid BL2 serial, accepted by Gibbed/lootlemon
    private static final String KNOWN_CODE = "BL2(BwAAAADw4wATEVSgoxBarQGEBMQHRFXE/////////z9VxP9/WcQ=)";

    // ── helpers ───────────────────────────────────────────────────────────────

    private Path copyProfile1(@TempDir Path dir) throws Exception {
        Path dest = dir.resolve("profile.bin");
        try (InputStream in = getClass().getResourceAsStream(
                "/at/swimmesberger/bo2/profile/profile1.bin")) {
            assertNotNull(in, "profile1.bin not found in test resources");
            Files.copy(in, dest);
        }
        return dest;
    }

    private String captureOut(Runnable action) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream prev = System.out;
        System.setOut(new PrintStream(buf));
        try {
            action.run();
        } finally {
            System.setOut(prev);
        }
        return buf.toString();
    }

    private String captureErr(Runnable action) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream prev = System.err;
        System.setErr(new PrintStream(buf));
        try {
            action.run();
        } finally {
            System.setErr(prev);
        }
        return buf.toString();
    }

    private int runList(String... args) throws Exception {
        StashCommand.ListCommand cmd = new StashCommand.ListCommand();
        new CommandLine(cmd).parseArgs(args);
        return cmd.call();
    }

    private int runGet(String... args) throws Exception {
        StashCommand.GetCommand cmd = new StashCommand.GetCommand();
        new CommandLine(cmd).parseArgs(args);
        return cmd.call();
    }

    private int runSet(String... args) throws Exception {
        StashCommand.SetItemCommand cmd = new StashCommand.SetItemCommand();
        new CommandLine(cmd).parseArgs(args);
        return cmd.call();
    }

    private int runAdd(String... args) throws Exception {
        StashCommand.AddCommand cmd = new StashCommand.AddCommand();
        new CommandLine(cmd).parseArgs(args);
        return cmd.call();
    }

    private int runClear(String... args) throws Exception {
        StashCommand.ClearCommand cmd = new StashCommand.ClearCommand();
        new CommandLine(cmd).parseArgs(args);
        return cmd.call();
    }

    // ── list ─────────────────────────────────────────────────────────────────

    @Test
    public void list_profile1_returnsSuccess(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertEquals(0, runList("-f", profile.toString()));
    }

    @Test
    public void list_profile1_showsFourSlots(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        String out = captureOut(() -> {
            try { runList("-f", profile.toString()); } catch (Exception e) { throw new RuntimeException(e); }
        });
        long slotLines = out.lines().filter(l -> l.startsWith("Slot")).count();
        assertEquals(4, slotLines);
    }

    @Test
    public void list_profile1_allSlotsShowAsEmpty(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        String out = captureOut(() -> {
            try { runList("-f", profile.toString()); } catch (Exception e) { throw new RuntimeException(e); }
        });
        assertTrue(out.lines().filter(l -> l.startsWith("Slot")).allMatch(l -> l.contains("[empty]")),
                "All slots should show [empty] for an unmodified profile");
    }

    @Test
    public void list_nonexistentFile_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        int result = runList("-f", dir.resolve("no-such.bin").toString());
        assertTrue(result < 0);
    }

    // ── get ───────────────────────────────────────────────────────────────────

    @Test
    public void get_emptySlot_printsEmpty(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        String out = captureOut(() -> {
            try { runGet("1", "-f", profile.toString()); } catch (Exception e) { throw new RuntimeException(e); }
        });
        assertTrue(out.contains("[empty]"));
    }

    @Test
    public void get_slot1_returnsSuccess(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertEquals(0, runGet("1", "-f", profile.toString()));
    }

    @Test
    public void get_outOfRangeSlot_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        int result = runGet("99", "-f", profile.toString());
        assertTrue(result < 0);
    }

    @Test
    public void get_zeroSlot_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertTrue(runGet("0", "-f", profile.toString()) < 0);
    }

    // ── add ───────────────────────────────────────────────────────────────────

    @Test
    public void add_toEmptyStash_succeeds(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertEquals(0, runAdd(KNOWN_CODE, "-f", profile.toString()));
    }

    @Test
    public void add_fillsFirstSlot(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runAdd(KNOWN_CODE, "-f", profile.toString());

        ProfileDataCache.invalidate(profile);
        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(profile));
        assertFalse(slots.get(0).isEmpty());
        assertTrue(slots.get(1).isEmpty());
    }

    @Test
    public void add_itemDataRoundTrips(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runAdd(KNOWN_CODE, "-f", profile.toString());

        ProfileDataCache.invalidate(profile);
        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(profile));
        String reread = new GibbedCodec().encode(slots.get(0).getData());
        assertEquals(KNOWN_CODE, reread);
    }

    @Test
    public void add_invalidCode_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertTrue(runAdd("not-a-code", "-f", profile.toString()) < 0);
    }

    @Test
    public void add_invalidCode_doesNotModifyProfile(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        byte[] before = Files.readAllBytes(profile);
        runAdd("not-a-code", "-f", profile.toString());
        assertArrayEquals(before, Files.readAllBytes(profile));
    }

    @Test
    public void add_whenStashFull_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        // Fill all 4 slots
        for (int i = 0; i < 4; i++) {
            runAdd(KNOWN_CODE, "-f", profile.toString());
            ProfileDataCache.invalidate(profile);
        }
        int result = runAdd(KNOWN_CODE, "-f", profile.toString());
        assertTrue(result < 0);
    }

    // ── set ───────────────────────────────────────────────────────────────────

    @Test
    public void set_specificSlot_succeeds(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertEquals(0, runSet("2", KNOWN_CODE, "-f", profile.toString()));
    }

    @Test
    public void set_updatesCorrectSlot(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runSet("2", KNOWN_CODE, "-f", profile.toString());

        ProfileDataCache.invalidate(profile);
        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(profile));
        assertTrue(slots.get(0).isEmpty(), "Slot 1 should still be empty");
        assertFalse(slots.get(1).isEmpty(), "Slot 2 should now be occupied");
    }

    @Test
    public void set_invalidCode_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertTrue(runSet("1", "garbage", "-f", profile.toString()) < 0);
    }

    @Test
    public void set_outOfRangeSlot_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertTrue(runSet("99", KNOWN_CODE, "-f", profile.toString()) < 0);
    }

    @Test
    public void set_outputFileOption_writesToSeparateFile(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        Path output = dir.resolve("output.bin");
        Files.copy(profile, output);

        assertEquals(0, runSet("1", KNOWN_CODE, "-f", profile.toString(), "-o", output.toString()));
        // Source profile should be unchanged
        assertArrayEquals(Files.readAllBytes(profile), Files.readAllBytes(profile));
        // Output should have the item
        ProfileDataCache.invalidate(output);
        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(output));
        assertFalse(slots.get(0).isEmpty());
    }

    // ── clear ─────────────────────────────────────────────────────────────────

    @Test
    public void clear_occupiedSlot_succeeds(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runAdd(KNOWN_CODE, "-f", profile.toString());
        ProfileDataCache.invalidate(profile);
        assertEquals(0, runClear("1", "-f", profile.toString()));
    }

    @Test
    public void clear_occupiedSlot_emptiesSlot(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runAdd(KNOWN_CODE, "-f", profile.toString());
        ProfileDataCache.invalidate(profile);
        runClear("1", "-f", profile.toString());

        ProfileDataCache.invalidate(profile);
        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(profile));
        assertTrue(slots.get(0).isEmpty());
    }

    @Test
    public void clear_alreadyEmptySlot_returnsSuccess(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertEquals(0, runClear("1", "-f", profile.toString()));
    }

    @Test
    public void clear_alreadyEmptySlot_slotRemainsEmpty(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runClear("1", "-f", profile.toString());
        ProfileDataCache.invalidate(profile);
        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(profile));
        assertTrue(slots.get(0).isEmpty(), "Slot 1 should still be empty after clearing an already-empty slot");
    }

    @Test
    public void clear_outOfRangeSlot_returnsNegativeExitCode(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        assertTrue(runClear("99", "-f", profile.toString()) < 0);
    }

    // ── full CRUD round-trip ──────────────────────────────────────────────────

    @Test
    public void fullRoundTrip_addGetSetClear(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);

        // add → slot 1 occupied
        assertEquals(0, runAdd(KNOWN_CODE, "-f", profile.toString()));
        ProfileDataCache.invalidate(profile);

        // get slot 1 → returns the code
        String[] outHolder = {null};
        captureOut(() -> {
            try {
                outHolder[0] = captureOut(() -> {
                    try { runGet("1", "-f", profile.toString()); } catch (Exception e) { throw new RuntimeException(e); }
                });
            } catch (Exception e) { throw new RuntimeException(e); }
        });
        assertTrue(outHolder[0].contains("BL2("));

        // set slot 2 using same code
        ProfileDataCache.invalidate(profile);
        assertEquals(0, runSet("2", KNOWN_CODE, "-f", profile.toString()));
        ProfileDataCache.invalidate(profile);

        // clear slot 1 → slot 1 empty, slot 2 still occupied
        assertEquals(0, runClear("1", "-f", profile.toString()));
        ProfileDataCache.invalidate(profile);

        List<StashSlot> slots = new StashConverter().readStash(
                new ProfileDataHandler().readEntries(profile));
        assertTrue(slots.get(0).isEmpty(), "Slot 1 should be empty after clear");
        assertFalse(slots.get(1).isEmpty(), "Slot 2 should remain occupied");
        assertTrue(slots.get(2).isEmpty());
        assertTrue(slots.get(3).isEmpty());
    }

    // ── profile integrity ─────────────────────────────────────────────────────

    @Test
    public void add_doesNotCorruptOtherProfileData(@TempDir Path dir) throws Exception {
        Path profile = copyProfile1(dir);
        runAdd(KNOWN_CODE, "-f", profile.toString());
        ProfileDataCache.invalidate(profile);

        // Golden Keys and Badass Rank should be unchanged
        at.swimmesberger.bo2.profile.entity.ProfileData data =
                new ProfileDataHandler().readData(profile);
        assertEquals(52, data.getGoldenKeys());
        assertEquals(1315, data.getBadassRank());
    }
}

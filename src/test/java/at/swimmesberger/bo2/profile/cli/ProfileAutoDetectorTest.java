package at.swimmesberger.bo2.profile.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileAutoDetectorTest {

    // ── getSaveDataBase ───────────────────────────────────────────────────────

    @Test
    public void getSaveDataBase_returnsNonNull() {
        Path base = ProfileAutoDetector.getSaveDataBase();
        assertNotNull(base, "getSaveDataBase() must return a path on all platforms");
    }

    @Test
    public void getSaveDataBase_containsBorderlands2Directory() {
        Path base = ProfileAutoDetector.getSaveDataBase();
        assertNotNull(base);
        assertTrue(base.toString().contains("Borderlands 2"),
                "Save data path should contain 'Borderlands 2', got: " + base);
    }

    // ── findAllProfiles(Path) ─────────────────────────────────────────────────

    @Test
    public void findAllProfiles_emptyDirectory_returnsEmpty(@TempDir Path tempDir) {
        List<Path> result = ProfileAutoDetector.findAllProfiles(tempDir);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllProfiles_directoryWithoutProfileBin_returnsEmpty(@TempDir Path tempDir) throws IOException {
        Path steamIdDir = tempDir.resolve("76561198000000001");
        Files.createDirectories(steamIdDir);
        Files.writeString(steamIdDir.resolve("save0001.sav"), "dummy");

        List<Path> result = ProfileAutoDetector.findAllProfiles(tempDir);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllProfiles_singleProfileBin_returnsSingleEntry(@TempDir Path tempDir) throws IOException {
        Path steamIdDir = tempDir.resolve("76561198000000001");
        Files.createDirectories(steamIdDir);
        Files.writeString(steamIdDir.resolve("profile.bin"), "dummy");

        List<Path> result = ProfileAutoDetector.findAllProfiles(tempDir);
        assertEquals(1, result.size());
        assertEquals("profile.bin", result.get(0).getFileName().toString());
    }

    @Test
    public void findAllProfiles_multipleProfileBins_returnsAllSorted(@TempDir Path tempDir) throws IOException {
        String id1 = "76561198000000001";
        String id2 = "76561198000000002";
        Files.createDirectories(tempDir.resolve(id1));
        Files.createDirectories(tempDir.resolve(id2));
        Files.writeString(tempDir.resolve(id1).resolve("profile.bin"), "dummy");
        Files.writeString(tempDir.resolve(id2).resolve("profile.bin"), "dummy");

        List<Path> result = ProfileAutoDetector.findAllProfiles(tempDir);
        assertEquals(2, result.size());
        assertTrue(result.get(0).toString().contains(id1));
        assertTrue(result.get(1).toString().contains(id2));
    }

    @Test
    public void findAllProfiles_ignoresNonDirectoryEntries(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("some-file.txt"), "not a directory");
        Path steamIdDir = tempDir.resolve("76561198000000001");
        Files.createDirectories(steamIdDir);
        Files.writeString(steamIdDir.resolve("profile.bin"), "dummy");

        List<Path> result = ProfileAutoDetector.findAllProfiles(tempDir);
        assertEquals(1, result.size());
    }
}

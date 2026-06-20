package at.swimmesberger.bo2.profile.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UndoCommandTest {

    private UndoCommand command(String... rawArgs) {
        UndoCommand cmd = new UndoCommand();
        new CommandLine(cmd).parseArgs(rawArgs);
        return cmd;
    }

    @Test
    public void undo_restoresMostRecentBackup(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "original");

        Path backup = tempDir.resolve("profile.bin.1000");
        Files.writeString(backup, "backup content");

        int result = command("-f", profile.toString()).call();

        assertEquals(0, result);
        assertEquals("backup content", Files.readString(profile));
    }

    @Test
    public void undo_choosesLatestWhenMultipleBackupsExist(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "current");

        Files.writeString(tempDir.resolve("profile.bin.1000"), "older");
        Files.writeString(tempDir.resolve("profile.bin.2000"), "newer");
        Files.writeString(tempDir.resolve("profile.bin.1500"), "middle");

        command("-f", profile.toString()).call();

        assertEquals("newer", Files.readString(profile));
    }

    @Test
    public void undo_noBackup_returnsNegativeExitCode(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "original");

        int result = command("-f", profile.toString()).call();

        assertTrue(result < 0);
    }

    @Test
    public void undo_noBackup_originalFileUnchanged(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "original");

        command("-f", profile.toString()).call();

        assertEquals("original", Files.readString(profile));
    }

    @Test
    public void undo_ignoresNonTimestampSuffixes(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "current");

        // These should NOT be treated as backups
        Files.writeString(tempDir.resolve("profile.bin.bak"), "not a backup");
        Files.writeString(tempDir.resolve("profile.bin.old"), "not a backup");

        int result = command("-f", profile.toString()).call();

        // No valid backup found
        assertTrue(result < 0);
        assertEquals("current", Files.readString(profile));
    }

    @Test
    public void undo_outputMentionsDate(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "current");
        Files.writeString(tempDir.resolve("profile.bin.1000000000000"), "backup");

        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(captured));
        try {
            command("-f", profile.toString()).call();
        } finally {
            System.setOut(original);
        }

        String output = captured.toString();
        assertTrue(output.contains("Restored from backup created:"),
                "Output should mention the restore date, got: " + output);
    }

    @Test
    public void undo_nonexistentFile_returnsNegativeExitCode(@TempDir Path tempDir) throws Exception {
        Path missing = tempDir.resolve("no-such-file.bin");

        int result = command("-f", missing.toString()).call();

        assertTrue(result < 0);
    }
}

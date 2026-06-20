package at.swimmesberger.bo2.profile.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BackupCommandTest {

    private BackupCommand command(String... rawArgs) {
        BackupCommand cmd = new BackupCommand();
        new CommandLine(cmd).parseArgs(rawArgs);
        return cmd;
    }

    private List<Path> findBackups(Path dir, String originalName) throws IOException {
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(p -> p.getFileName().toString().startsWith(originalName + "."))
                    .collect(Collectors.toList());
        }
    }

    @Test
    public void backup_createsTimestampedFileNextToOriginal(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "dummy content");

        int result = command("-f", profile.toString()).call();

        assertEquals(0, result);
        List<Path> backups = findBackups(tempDir, "profile.bin");
        assertEquals(1, backups.size());
    }

    @Test
    public void backup_backupContentsMatchOriginal(@TempDir Path tempDir) throws Exception {
        byte[] original = {0x01, 0x02, 0x03, 0x04};
        Path profile = tempDir.resolve("profile.bin");
        Files.write(profile, original);

        int result = command("-f", profile.toString()).call();

        assertEquals(0, result);
        Path backup = findBackups(tempDir, "profile.bin").get(0);
        assertArrayEquals(original, Files.readAllBytes(backup));
    }

    @Test
    public void backup_originalFileIsUnmodified(@TempDir Path tempDir) throws Exception {
        String content = "original data";
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, content);

        command("-f", profile.toString()).call();

        assertEquals(content, Files.readString(profile));
    }

    @Test
    public void backup_backupNameHasTimestampSuffix(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "dummy");

        command("-f", profile.toString()).call();

        Path backup = findBackups(tempDir, "profile.bin").get(0);
        String suffix = backup.getFileName().toString().substring("profile.bin.".length());
        assertTrue(suffix.matches("\\d+"), "Suffix should be all digits, got: " + suffix);
    }

    @Test
    public void backup_twoConsecutiveRuns_createsTwoDistinctBackups(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "dummy");

        command("-f", profile.toString()).call();
        // Small delay to guarantee distinct millisecond timestamps
        Thread.sleep(2);
        command("-f", profile.toString()).call();

        List<Path> backups = findBackups(tempDir, "profile.bin");
        assertEquals(2, backups.size());
        assertNotEquals(backups.get(0).getFileName(), backups.get(1).getFileName());
    }

    @Test
    public void backup_nonexistentFile_returnsNegativeExitCode(@TempDir Path tempDir) throws Exception {
        Path missing = tempDir.resolve("no-such-file.bin");

        int result = command("-f", missing.toString()).call();

        assertTrue(result < 0);
    }

    @Test
    public void backup_nonexistentFile_createsNoBackup(@TempDir Path tempDir) throws Exception {
        Path missing = tempDir.resolve("no-such-file.bin");

        command("-f", missing.toString()).call();

        List<Path> backups = findBackups(tempDir, "no-such-file.bin");
        assertTrue(backups.isEmpty());
    }

    @Test
    public void backup_outputIncludesBackupFilePath(@TempDir Path tempDir) throws Exception {
        Path profile = tempDir.resolve("profile.bin");
        Files.writeString(profile, "dummy");

        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(captured));
        try {
            command("-f", profile.toString()).call();
        } finally {
            System.setOut(original);
        }

        String output = captured.toString();
        assertTrue(output.contains("profile.bin."), "Output should contain backup file name, got: " + output);
    }
}

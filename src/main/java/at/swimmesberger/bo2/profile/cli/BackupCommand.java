package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "backup", mixinStandardHelpOptions = true,
        version = "backup 1.0",
        description = "Creates a timestamped backup of the profile file next to the original.")
public class BackupCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-f", "--file"},
            description = "Path to profile.bin. If omitted, auto-detected from the default Borderlands 2 save location.")
    private Path inputFile;

    @Override
    public Integer call() {
        try {
            Path profilePath = resolveInputFile();
            if (profilePath == null) return -1;

            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            Path backup = profilePath.resolveSibling(profilePath.getFileName() + "." + timestamp);
            Files.copy(profilePath, backup, StandardCopyOption.COPY_ATTRIBUTES);
            System.out.println("Backup saved: " + backup.toAbsolutePath());
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Path resolveInputFile() {
        if (this.inputFile != null) {
            return this.inputFile;
        }
        return ProfileAutoDetector.detectProfilePath().orElseGet(() -> {
            System.err.println("Error: Could not auto-detect profile.bin. Specify the path with -f <file>.");
            return null;
        });
    }
}

package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@CommandLine.Command(name = "undo", mixinStandardHelpOptions = true,
        version = "undo 1.0",
        description = "Restores the most recent backup of the profile file created by 'bl2 backup'.")
public class UndoCommand implements Callable<Integer> {
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\d+");
    @CommandLine.Option(names = {"-f", "--file"},
            description = "Path to profile.bin. If omitted, auto-detected from the default Borderlands 2 save location.")
    private Path inputFile;

    @Override
    public Integer call() {
        try {
            Path profilePath = resolveInputFile();
            if (profilePath == null) return -1;

            Path dir = profilePath.getParent();
            String name = profilePath.getFileName().toString();
            String prefix = name + ".";

            Optional<Path> latest;
            try (Stream<Path> files = Files.list(dir)) {
                latest = files
                        .filter(p -> p.getFileName().toString().startsWith(prefix))
                        .filter(p -> TIMESTAMP_PATTERN.matcher(p.getFileName().toString().substring(prefix.length())).matches())
                        .max(Comparator.comparingLong(p ->
                                Long.parseLong(p.getFileName().toString().substring(prefix.length()))));
            }

            if (!latest.isPresent()) {
                System.err.println("No backup found to restore.");
                System.err.println("Run 'bl2 backup' before editing to create one.");
                return -1;
            }

            Files.copy(latest.get(), profilePath, StandardCopyOption.REPLACE_EXISTING);

            long ts = Long.parseLong(latest.get().getFileName().toString().substring(prefix.length()));
            String date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.ofEpochMilli(ts));

            System.out.println("Restored from backup created: " + date);
            System.out.println("Run 'bl2 get' to verify.");
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Path resolveInputFile() {
        if (this.inputFile != null) return this.inputFile;
        return ProfileAutoDetector.detectProfilePath().orElseGet(() -> {
            System.err.println("Error: Could not auto-detect profile.bin. Specify the path with -f <file>.");
            return null;
        });
    }
}

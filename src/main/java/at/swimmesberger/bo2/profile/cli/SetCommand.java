package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import at.swimmesberger.bo2.profile.entity.ProfileStats;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "set", mixinStandardHelpOptions = true,
        version = "set 1.0",
        description = {
                "Sets one or more values in the profile file in a single backup and write.",
                "  set <value>                       Set ALL stats to that value.",
                "  set <TYPE> <value>                Set one stat.",
                "  set <TYPE> <value> <TYPE> <value> Set multiple stats at once.",
                "Use 'max' as the value for the maximum safe BL2 value."
        })
public class SetCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-f", "--file"},
            description = "Path to profile.bin. If omitted, auto-detected from the default Borderlands 2 save location.")
    private Path inputFile;

    @CommandLine.Parameters(arity = "1..*",
            description = "[TYPE value]... or <value>  — omit TYPE to set all stats.")
    private List<String> args;

    @CommandLine.Option(names = {"-o", "--out"}, description = "Output file (default: overwrite input file)")
    private Path outputFile;

    @Override
    public Integer call() {
        try {
            Path inputFile = resolveInputFile();
            if (inputFile == null) return -1;

            Path outputFile = this.outputFile != null ? this.outputFile : inputFile;

            Map<ProfileDataValueType, String> values = buildValueMap();
            if (values == null) return -1;

            if (outputFile.toAbsolutePath().equals(inputFile.toAbsolutePath())) {
                createBackup(inputFile);
            }

            new ProfileDataHandler().setValues(inputFile, outputFile, values);

            int maxLen = values.keySet().stream().mapToInt(t -> t.name().length()).max().orElse(0);
            String fmt = "Set %-" + maxLen + "s = %s%n";
            values.forEach((type, value) -> System.out.printf(fmt, type, value));
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Map<ProfileDataValueType, String> buildValueMap() {
        if (args.size() == 1) {
            // bulk: set all types to this value
            Map<ProfileDataValueType, String> values = new LinkedHashMap<>();
            for (ProfileDataValueType type : ProfileDataValueType.values()) {
                values.put(type, resolveValue(type, args.get(0)));
            }
            return values;
        }

        if (args.size() % 2 != 0) {
            System.err.println("Error: arguments must be pairs of TYPE and value (e.g. GOLDEN_KEYS 255 BADASS_RANK max).");
            return null;
        }

        // pairs: TYPE value TYPE value ...
        Map<ProfileDataValueType, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i += 2) {
            String typeName = args.get(i).toUpperCase();
            String rawValue = args.get(i + 1);
            ProfileDataValueType type;
            try {
                type = ProfileDataValueType.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Unknown value type '" + args.get(i) + "'.");
                System.err.println("Available: " + java.util.Arrays.toString(ProfileDataValueType.values()));
                return null;
            }
            values.put(type, resolveValue(type, rawValue));
        }
        return values;
    }

    private String resolveValue(ProfileDataValueType type, String rawValue) {
        if ("max".equalsIgnoreCase(rawValue)) {
            return maxValue(type);
        }
        return rawValue;
    }

    private String maxValue(ProfileDataValueType type) {
        switch (type) {
            case GOLDEN_KEYS:        return "255";
            case BADASS_RANK:        return "2000000000";
            case BADASS_TOKENS:      return "500";
            case ALL_CUSTOMIZATIONS: return "true";
            default:                 return String.valueOf(ProfileStats.MAXIMUM_STAT_VALUE);
        }
    }

    private void createBackup(Path profilePath) throws IOException {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        Path backup = profilePath.resolveSibling(profilePath.getFileName() + "." + timestamp);
        Files.copy(profilePath, backup, StandardCopyOption.COPY_ATTRIBUTES);
        System.out.println("Backup saved: " + backup.getFileName());
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

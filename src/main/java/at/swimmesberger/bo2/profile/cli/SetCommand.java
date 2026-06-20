package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import at.swimmesberger.bo2.profile.entity.ProfileStats;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "set", mixinStandardHelpOptions = true,
        version = "set 1.0",
        description = {
                "Sets one or more values in the profile file in a single backup and write.",
                "  set all <value>                    Set ALL stats to that value.",
                "  set <TYPE> <value>                 Set one stat.",
                "  set <TYPE> <value> <TYPE> <value>  Set multiple stats at once.",
                "Use 'max' as the value for the maximum safe BL2 value for each stat."
        })
public class SetCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-f", "--file"},
            description = "Path to profile.bin. If omitted, auto-detected from the default Borderlands 2 save location.")
    private Path inputFile;

    @CommandLine.Parameters(arity = "2..*",
            description = "'all <value>' or [TYPE value]...  — use 'all' to set every stat at once.")
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

            ProfileData before = ProfileDataCache.getOrLoad(inputFile);
            ProfileEntries entries = ProfileDataCache.getOrLoadEntries(inputFile);
            new ProfileDataHandler().setValues(entries, outputFile, values);
            ProfileDataCache.invalidate(outputFile);

            int maxLen = values.keySet().stream().mapToInt(t -> t.name().length()).max().orElse(0);
            boolean anyChanged = false;
            for (Map.Entry<ProfileDataValueType, String> entry : values.entrySet()) {
                ProfileDataValueType type = entry.getKey();
                String oldVal = ValueFormatter.format(type, String.valueOf(before.getValue(type)));
                String newVal = ValueFormatter.format(type, entry.getValue());
                if (!oldVal.equals(newVal)) {
                    System.out.printf("%-" + maxLen + "s  %s → %s%n", type.name(), oldVal, newVal);
                    anyChanged = true;
                }
            }
            if (!anyChanged) {
                System.out.println("No values changed.");
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid numeric value — " + e.getMessage());
            return -1;
        }
    }

    Map<ProfileDataValueType, String> buildValueMap() {
        if ("all".equalsIgnoreCase(args.get(0))) {
            if (args.size() != 2) {
                System.err.println("Error: 'all' takes exactly one value (e.g. set all max).");
                return null;
            }
            String rawValue = args.get(1);
            Map<ProfileDataValueType, String> values = new LinkedHashMap<>();
            for (ProfileDataValueType type : ProfileDataValueType.values()) {
                values.put(type, resolveValue(type, rawValue));
            }
            return values;
        }

        if (args.size() % 2 != 0) {
            System.err.println("Error: arguments must be pairs of TYPE and value (e.g. GOLDEN_KEYS 255 BADASS_RANK max), or 'all <value>'.");
            return null;
        }

        Map<ProfileDataValueType, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i += 2) {
            String typeName = args.get(i).toUpperCase();
            String rawValue = args.get(i + 1);
            ProfileDataValueType type;
            try {
                type = ProfileDataValueType.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Unknown value type '" + args.get(i) + "'.");
                System.err.println("Available: " + Arrays.toString(ProfileDataValueType.values()));
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

    String maxValue(ProfileDataValueType type) {
        switch (type) {
            case GOLDEN_KEYS:        return "255";
            case BADASS_RANK:        return "2000000000";
            case BADASS_TOKENS:      return "500";
            case ALL_CUSTOMIZATIONS: return "true";
            default:                 return String.valueOf(ProfileStats.MAXIMUM_STAT_VALUE);
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

package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "get", mixinStandardHelpOptions = true,
        version = "get 1.0",
        description = "Gets one or more values from the profile file. Omit VALUE_TYPE to print all values.")
public class GetCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-f", "--file"},
            description = "Path to profile.bin. If omitted, auto-detected from the default Borderlands 2 save location.")
    private Path inputFile;

    @CommandLine.Parameters(arity = "0..*",
            description = "One or more value types to read (available: ${COMPLETION-CANDIDATES}). Omit to print all.")
    private List<ProfileDataValueType> valueTypes;

    @Override
    public Integer call() {
        try {
            Path inputFile = resolveInputFile();
            if (inputFile == null) return -1;

            ProfileDataHandler dataHandler = new ProfileDataHandler();
            ProfileData data = dataHandler.readData(inputFile);

            List<ProfileDataValueType> types = (valueTypes == null || valueTypes.isEmpty())
                    ? Arrays.asList(ProfileDataValueType.values())
                    : valueTypes;

            int maxLen = types.stream().mapToInt(t -> t.name().length()).max().orElse(0);
            String fmt = "%-" + maxLen + "s = %s%n";
            for (ProfileDataValueType type : types) {
                System.out.printf(fmt, type.name(), data.getValue(type));
            }
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

package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "change-profile", mixinStandardHelpOptions = true,
        version = "change-profile 1.0",
        description = {
                "Change which Borderlands 2 save profile is active.",
                "  change-profile                 List all detected profiles and prompt for selection.",
                "  change-profile <id>            Use the profile matching this Steam ID.",
                "  change-profile <path>          Use the save folder or profile.bin at this path."
        })
public class ChangeProfileCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity = "0..1",
            description = "Steam ID, full path to a save folder, or full path to a profile.bin file. Omit to see all options.")
    private String folder;

    @Override
    public Integer call() {
        if (folder == null) {
            return selectFromDetected();
        }
        return selectByName(folder);
    }

    private int selectFromDetected() {
        List<Path> profiles = ProfileAutoDetector.findAllProfiles();
        if (profiles.isEmpty()) {
            System.err.println("No Borderlands 2 save profiles found.");
            System.err.println("Use 'change-profile <path>' to specify one manually.");
            return -1;
        }
        Path selected = profiles.size() == 1 ? profiles.get(0) : ProfileAutoDetector.promptSelection(profiles);
        return save(selected);
    }

    private int selectByName(String input) {
        // Try as a Steam ID under every known SaveData directory (covers all launchers/platforms)
        for (Path base : ProfileAutoDetector.getSaveDataBases()) {
            Path byId = base.resolve(input).resolve("profile.bin");
            if (Files.exists(byId)) {
                return save(byId);
            }
        }

        // Try as a direct path — accepts a folder containing profile.bin or the file itself
        Path direct = Paths.get(input);
        if (Files.isDirectory(direct)) {
            direct = direct.resolve("profile.bin");
        }
        if (Files.exists(direct) && direct.getFileName().toString().equals("profile.bin")) {
            return save(direct);
        }

        System.err.println("Error: No profile.bin found for '" + input + "'.");
        System.err.println("You can provide a Steam ID, a save folder path, or a direct path to profile.bin.");
        return -1;
    }

    private int save(Path profileBin) {
        try {
            ProfileConfig.saveProfilePath(profileBin);
            printScreen(profileBin);
            return 0;
        } catch (IOException e) {
            System.err.println("Error saving profile path: " + e.getMessage());
            return -1;
        }
    }

    private void printScreen(Path profileBin) throws IOException {
        ProfileData data = ProfileDataCache.getOrLoad(profileBin);

        String profileId = profileBin.getParent() != null
                ? profileBin.getParent().getFileName().toString()
                : profileBin.toAbsolutePath().toString();

        System.out.println("============================================");
        System.out.println("   Borderlands 2 Profile Editor");
        System.out.println("============================================");
        System.out.println();
        System.out.println("Profile: " + profileId);
        System.out.println();

        ProfileDataValueType[] types = ProfileDataValueType.values();
        int maxLen = Arrays.stream(types).mapToInt(t -> t.name().length()).max().orElse(0);
        String fmt = "%-" + maxLen + "s = %s%n";
        for (ProfileDataValueType type : types) {
            System.out.printf(fmt, type.name(), ValueFormatter.format(type, String.valueOf(data.getValue(type))));
        }

        System.out.println();
        System.out.println("--------------------------------------------");
        System.out.println("Commands:");
        System.out.println("  bl2 backup");
        System.out.println("  bl2 undo");
        System.out.println("  bl2 get");
        System.out.println("  bl2 set all max");
        System.out.println("  bl2 set GOLDEN_KEYS max BADASS_RANK max");
        if (ProfileAutoDetector.hasMultipleProfiles()) {
            System.out.println("  bl2 change-profile");
        }
        System.out.println("Type 'exit' to close this window.");
        System.out.println("--------------------------------------------");
        System.out.println();
        System.out.println("Always run bl2 backup before editing.");
        System.out.println();
    }
}

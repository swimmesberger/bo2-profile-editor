package at.swimmesberger.bo2.profile.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProfileAutoDetector {

    public static Optional<Path> detectProfilePath() {
        // Saved config takes priority
        Optional<Path> saved = ProfileConfig.getSavedProfilePath();
        if (saved.isPresent()) {
            return saved;
        }

        Path base = getSaveDataBase();
        if (base == null || !Files.exists(base)) {
            return Optional.empty();
        }

        List<Path> profiles = findAllProfiles(base);
        if (profiles.isEmpty()) {
            return Optional.empty();
        }

        Path selected = profiles.size() == 1 ? profiles.get(0) : promptSelection(profiles);

        try {
            ProfileConfig.saveProfilePath(selected);
            System.out.println("Profile saved to config: " + selected);
        } catch (IOException e) {
            // non-fatal
        }

        return Optional.of(selected);
    }

    public static List<Path> findAllProfiles() {
        Path base = getSaveDataBase();
        if (base == null || !Files.exists(base)) return Collections.emptyList();
        return findAllProfiles(base);
    }

    public static Path getSaveDataBase() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", "Borderlands 2", "WillowGame", "SaveData");
        } else if (os.contains("win")) {
            String userProfile = System.getenv("USERPROFILE");
            String base = (userProfile != null && !userProfile.isEmpty()) ? userProfile : home;
            return Paths.get(base, "Documents", "My Games", "Borderlands 2", "WillowGame", "SaveData");
        } else {
            return Paths.get(home, ".local", "share", "Steam", "steamapps", "compatdata", "49520",
                    "pfx", "drive_c", "users", "steamuser", "Documents", "My Games",
                    "Borderlands 2", "WillowGame", "SaveData");
        }
    }

    public static Path promptSelection(List<Path> profiles) {
        System.out.println("Multiple Borderlands 2 profiles found. Select one:");
        for (int i = 0; i < profiles.size(); i++) {
            String steamId = profiles.get(i).getParent().getFileName().toString();
            System.out.printf("  [%d] %s%n", i + 1, steamId);
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.printf("Enter choice [1-%d]: ", profiles.size());
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= profiles.size()) {
                    return profiles.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid selection, please try again.");
        }
    }

    private static List<Path> findAllProfiles(Path saveDataBase) {
        try {
            return Files.list(saveDataBase)
                    .filter(Files::isDirectory)
                    .map(dir -> dir.resolve("profile.bin"))
                    .filter(Files::exists)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}

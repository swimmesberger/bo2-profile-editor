package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileAutoDetector {

    public static Optional<Path> detectProfilePath() {
        // Saved config takes priority; use 'change-profile' to update it.
        Optional<Path> saved = ProfileConfig.getSavedProfilePath();
        if (saved.isPresent()) {
            return saved;
        }

        List<Path> profiles = findAllProfiles();
        if (profiles.isEmpty()) {
            return Optional.empty();
        }

        Path selected = profiles.size() == 1 ? profiles.get(0) : promptSelection(profiles);
        try {
            ProfileConfig.saveProfilePath(selected);
        } catch (IOException ignored) {
            // non-fatal — user will be re-prompted on next run if config can't be written
        }
        return Optional.of(selected);
    }

    public static List<Path> findAllProfiles() {
        List<Path> all = new ArrayList<>();
        for (Path base : getSaveDataBases()) {
            all.addAll(findAllProfiles(base));
        }
        return all;
    }

    private static List<Path> cachedBases;

    /**
     * Returns all candidate SaveData directories for the current OS, covering
     * Steam, Epic, GOG, Flatpak Steam, Snap Steam, and OneDrive-redirected Documents.
     * Candidates are returned whether or not they currently exist on disk.
     * Result is memoized — OS and environment variables do not change per process.
     */
    public static List<Path> getSaveDataBases() {
        if (cachedBases == null) {
            cachedBases = Collections.unmodifiableList(computeSaveDataBases());
        }
        return cachedBases;
    }

    private static List<Path> computeSaveDataBases() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home");
        List<Path> candidates = new ArrayList<>();

        if (os.contains("mac")) {
            // Steam (only relevant launcher for BL2 on macOS)
            candidates.add(Paths.get(home, "Library", "Application Support",
                    "Borderlands 2", "WillowGame", "SaveData"));

        } else if (os.contains("win")) {
            String userProfile = System.getenv("USERPROFILE");
            String base = (userProfile != null && !userProfile.isEmpty()) ? userProfile : home;

            // Steam / Epic / GOG all write to the same Documents path on Windows
            candidates.add(Paths.get(base, "Documents", "My Games",
                    "Borderlands 2", "WillowGame", "SaveData"));

            // OneDrive can redirect the Documents folder to a different location
            String oneDrive = System.getenv("OneDrive");
            if (oneDrive != null && !oneDrive.isEmpty()) {
                candidates.add(Paths.get(oneDrive, "Documents", "My Games",
                        "Borderlands 2", "WillowGame", "SaveData"));
            }

        } else {
            // Linux — BL2 runs exclusively via Steam/Proton (no native port).
            // Check all common Steam installation locations.
            String[] steamRoots = {
                // Standard Steam installation
                Paths.get(home, ".local", "share", "Steam").toString(),
                // Flatpak Steam (most common alternative)
                Paths.get(home, ".var", "app", "com.valvesoftware.Steam",
                        ".local", "share", "Steam").toString(),
                // Snap Steam
                Paths.get(home, "snap", "steam", "common",
                        ".local", "share", "Steam").toString()
            };
            for (String steamRoot : steamRoots) {
                candidates.add(Paths.get(steamRoot, "steamapps", "compatdata", "49520",
                        "pfx", "drive_c", "users", "steamuser", "Documents",
                        "My Games", "Borderlands 2", "WillowGame", "SaveData"));
            }
        }

        return candidates;
    }

    /** Returns the primary save data base for backward compatibility. */
    public static Path getSaveDataBase() {
        List<Path> bases = getSaveDataBases();
        return bases.isEmpty() ? null : bases.get(0);
    }

    public static Path promptSelection(List<Path> profiles) {
        System.out.println("Multiple Borderlands 2 profiles found. Select one:");
        for (int i = 0; i < profiles.size(); i++) {
            String steamId = profiles.get(i).getParent().getFileName().toString();
            String stats = quickStats(profiles.get(i));
            System.out.printf("  [%d] %s%s%n", i + 1, steamId, stats);
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

    private static String quickStats(Path profileBin) {
        try {
            ProfileData data = ProfileDataCache.getOrLoad(profileBin);
            String keys = ValueFormatter.format(ProfileDataValueType.GOLDEN_KEYS,
                    String.valueOf(data.getValue(ProfileDataValueType.GOLDEN_KEYS)));
            String rank = ValueFormatter.format(ProfileDataValueType.BADASS_RANK,
                    String.valueOf(data.getValue(ProfileDataValueType.BADASS_RANK)));
            return "  —  Keys: " + keys + "  Rank: " + rank;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns true as soon as 2 profiles are found across all save data bases, without loading
     * the full list. Avoids scanning remaining directories once the threshold is reached.
     */
    public static boolean hasMultipleProfiles() {
        int count = 0;
        for (Path base : getSaveDataBases()) {
            if (!Files.exists(base)) continue;
            try (Stream<Path> stream = Files.list(base)) {
                long found = stream
                        .filter(Files::isDirectory)
                        .filter(dir -> Files.exists(dir.resolve("profile.bin")))
                        .limit(2 - count)
                        .count();
                count += (int) found;
                if (count >= 2) return true;
            } catch (IOException ignored) {}
        }
        return false;
    }

    static List<Path> findAllProfiles(Path saveDataBase) {
        if (!Files.exists(saveDataBase)) return Collections.emptyList();
        try (Stream<Path> stream = Files.list(saveDataBase)) {
            return stream
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

package at.swimmesberger.bo2.profile.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class ProfileConfig {
    private static final String KEY_PROFILE_PATH = "profile.path";
    private static Path cachedConfigDir;

    public static Optional<Path> getSavedProfilePath() {
        Path config = configFile();
        if (!Files.exists(config)) return Optional.empty();
        try {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(config)) {
                props.load(in);
            }
            String raw = props.getProperty(KEY_PROFILE_PATH);
            if (raw == null || raw.trim().isEmpty()) return Optional.empty();
            Path p = Paths.get(raw.trim());
            return Files.exists(p) ? Optional.of(p) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static void saveProfilePath(Path profileBin) throws IOException {
        Path config = configFile();
        Files.createDirectories(config.getParent());
        Properties props = new Properties();
        props.setProperty(KEY_PROFILE_PATH, profileBin.toAbsolutePath().toString());
        try (OutputStream out = Files.newOutputStream(config)) {
            props.store(out, "Borderlands 2 Profile Editor — saved profile location");
        }
    }

    static Path configFile() {
        return configDir().resolve("bl2.config");
    }

    private static Path configDir() {
        if (cachedConfigDir == null) {
            cachedConfigDir = computeConfigDir();
        }
        return cachedConfigDir;
    }

    private static Path computeConfigDir() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", "bl2-profile-editor");
        } else if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            String base = (appData != null && !appData.isEmpty()) ? appData : home;
            return Paths.get(base, "bl2-profile-editor");
        } else {
            String xdg = System.getenv("XDG_CONFIG_HOME");
            String base = (xdg != null && !xdg.isEmpty()) ? xdg : Paths.get(home, ".config").toString();
            return Paths.get(base, "bl2-profile-editor");
        }
    }
}

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
    private static final Path CONFIG_FILE = Paths.get("bl2.config");
    private static final String KEY_PROFILE_PATH = "profile.path";

    public static Optional<Path> getSavedProfilePath() {
        if (!Files.exists(CONFIG_FILE)) return Optional.empty();
        try {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
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
        Properties props = new Properties();
        props.setProperty(KEY_PROFILE_PATH, profileBin.toAbsolutePath().toString());
        try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
            props.store(out, "Borderlands 2 Profile Editor — saved profile location");
        }
    }
}

package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "change-folder", mixinStandardHelpOptions = true,
        version = "change-folder 1.0",
        description = {
                "Change which Borderlands 2 save folder is used.",
                "  change-folder            List all detected folders and prompt for selection.",
                "  change-folder <id>       Use the folder matching this Steam ID or path."
        })
public class ChangeFolderCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity = "0..1",
            description = "Steam ID folder name or full path to a save folder. Omit to see all options.")
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
            System.err.println("No Borderlands 2 save folders found.");
            System.err.println("Use 'change-folder <path>' to specify one manually.");
            return -1;
        }
        Path selected = profiles.size() == 1 ? profiles.get(0) : ProfileAutoDetector.promptSelection(profiles);
        return save(selected);
    }

    private int selectByName(String input) {
        // Try as a Steam ID under the default SaveData directory
        Path base = ProfileAutoDetector.getSaveDataBase();
        if (base != null) {
            Path byId = base.resolve(input).resolve("profile.bin");
            if (Files.exists(byId)) {
                return save(byId);
            }
        }

        // Try as a direct path (folder or profile.bin itself)
        Path direct = Paths.get(input);
        if (Files.isDirectory(direct)) {
            direct = direct.resolve("profile.bin");
        }
        if (Files.exists(direct) && direct.getFileName().toString().equals("profile.bin")) {
            return save(direct);
        }

        System.err.println("Error: No profile.bin found for '" + input + "'.");
        System.err.println("Run 'change-folder' with no argument to see available options.");
        return -1;
    }

    private int save(Path profileBin) {
        try {
            ProfileConfig.saveProfilePath(profileBin);
            System.out.println("Active profile set to: " + profileBin.toAbsolutePath());
            return 0;
        } catch (IOException e) {
            System.err.println("Error saving profile path: " + e.getMessage());
            return -1;
        }
    }
}

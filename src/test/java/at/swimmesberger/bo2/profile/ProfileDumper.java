package at.swimmesberger.bo2.profile;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;

public class ProfileDumper {

    private static final String SAVE_DIR =
        System.getProperty("user.home") +
        "/Library/Application Support/Borderlands 2/WillowGame/SaveData/76561199125000821/";

    @Test
    public void dumpAllProfiles() throws Exception {
        String[] files = {
            "profile.bin",
            "profile.bin.1781958576639",
            "profile.bin.bak"
        };
        for (String f : files) {
            Path p = Paths.get(SAVE_DIR + f);
            if (!Files.exists(p)) {
                System.out.println("\n=== " + f + " (not found, skipped) ===");
                continue;
            }
            System.out.println("\n=== " + f + " (" + Files.size(p) + " bytes) ===");
            try {
                ProfileEntries entries = new ProfileEntryDataHandler().readEntries(
                        p, EntriesContainerFormat.COMPRESSED_LZO);
                entries.getEntries().stream()
                    .filter(e -> e != null && e.getDataType() == ProfileEntryDataType.Binary)
                    .forEach(e -> {
                        byte[] d = (byte[]) e.getValue();
                        boolean allZero = d.length > 0;
                        for (byte b : d) if (b != 0) { allZero = false; break; }
                        System.out.printf("  ID=%-5d Binary[%3d] %s%n",
                            e.getId(), d.length,
                            d.length == 0 ? "(empty 0-byte)" :
                            allZero       ? "(all zeros)"    :
                            hex(d, 20));
                    });
            } catch (Exception ex) {
                System.out.println("  ERROR: " + ex);
            }
        }
    }

    static String hex(byte[] b, int max) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(b.length, max); i++)
            sb.append(String.format("%02x ", b[i]));
        if (b.length > max) sb.append("...(+" + (b.length - max) + "bytes)");
        return sb.toString().trim();
    }
}

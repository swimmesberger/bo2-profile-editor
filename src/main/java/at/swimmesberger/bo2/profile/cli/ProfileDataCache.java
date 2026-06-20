package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class ProfileDataCache {
    private static final Map<Path, Entry> cache = new HashMap<>();

    static ProfileData getOrLoad(Path path) throws IOException {
        return getOrLoadEntry(path).data;
    }

    static ProfileEntries getOrLoadEntries(Path path) throws IOException {
        return getOrLoadEntry(path).entries;
    }

    static void invalidate(Path path) {
        cache.remove(path.toAbsolutePath().normalize());
    }

    private static Entry getOrLoadEntry(Path path) throws IOException {
        Path key = path.toAbsolutePath().normalize();
        Entry hit = cache.get(key);
        if (hit != null) return hit;
        ProfileDataHandler handler = new ProfileDataHandler();
        ProfileEntries entries = handler.readEntries(key);
        ProfileData data = handler.decodeEntries(entries);
        Entry entry = new Entry(entries, data);
        cache.put(key, entry);
        return entry;
    }

    private static final class Entry {
        final ProfileEntries entries;
        final ProfileData data;

        Entry(ProfileEntries entries, ProfileData data) {
            this.entries = entries;
            this.data = data;
        }
    }
}

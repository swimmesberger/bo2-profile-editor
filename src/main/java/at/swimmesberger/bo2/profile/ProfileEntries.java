package at.swimmesberger.bo2.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileEntries {
    private final Map<Integer, ProfileEntry<?>> idToEntry;

    public ProfileEntries(Map<Integer, ProfileEntry<?>> idToEntry) {
        this.idToEntry = idToEntry;
    }

    public static ProfileEntries from(List<ProfileEntry<?>> entries) {
        Map<Integer, ProfileEntry<?>> idToEntry = new HashMap<>(entries.size());
        for(ProfileEntry<?> entry : entries) {
            idToEntry.put(entry.getId(), entry);
        }
        return new ProfileEntries(idToEntry);
    }

    public ProfileEntry<?> getEntry(int id) {
        return this.idToEntry.get(id);
    }

    @Override
    public String toString() {
        return "ProfileEntries{" +
                "idToEntry=" + idToEntry +
                '}';
    }
}

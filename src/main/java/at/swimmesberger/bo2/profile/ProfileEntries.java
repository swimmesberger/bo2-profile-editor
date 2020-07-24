package at.swimmesberger.bo2.profile;

import java.util.*;

public class ProfileEntries {
    private final Map<Integer, ProfileEntry<?>> idToEntry;

    public ProfileEntries(Map<Integer, ProfileEntry<?>> idToEntry) {
        this.idToEntry = idToEntry;
    }


    public static ProfileEntriesBuilder builder() {
        return new ProfileEntriesBuilder();
    }

    public static ProfileEntriesBuilder builder(ProfileEntries entries) {
        return new ProfileEntriesBuilder(entries);
    }

    public static ProfileEntries from(List<ProfileEntry<?>> entries) {
        Map<Integer, ProfileEntry<?>> idToEntry = new HashMap<>(entries.size());
        for (ProfileEntry<?> entry : entries) {
            idToEntry.put(entry.getId(), entry);
        }
        return new ProfileEntries(idToEntry);
    }

    public ProfileEntry<?> getEntry(int id) {
        return this.idToEntry.get(id);
    }

    public Collection<ProfileEntry<?>> getEntries() {
        return this.idToEntry.values();
    }

    public List<ProfileEntry<?>> getEntriesList() {
        return new ArrayList<>(this.getEntries());
    }

    @Override
    public String toString() {
        return "ProfileEntries{" +
                "idToEntry=" + idToEntry +
                '}';
    }


    public static final class ProfileEntriesBuilder {
        private Map<Integer, ProfileEntry<?>> idToEntry;

        private ProfileEntriesBuilder() {
            this(null);
        }

        private ProfileEntriesBuilder(ProfileEntries entries) {
            if (entries != null) {
                this.idToEntry = new LinkedHashMap<>(entries.idToEntry);
            } else {
                this.idToEntry = new LinkedHashMap<>();
            }
        }

        public ProfileEntriesBuilder withIdToEntry(Map<Integer, ProfileEntry<?>> idToEntry) {
            this.idToEntry = idToEntry;
            return this;
        }

        public ProfileEntriesBuilder withEntry(ProfileEntry<?> entry) {
            this.idToEntry.put(entry.getId(), entry);
            return this;
        }

        public ProfileEntry<?> getEntry(int id) {
            return this.idToEntry.get(id);
        }

        public ProfileEntries build() {
            return new ProfileEntries(idToEntry);
        }
    }
}

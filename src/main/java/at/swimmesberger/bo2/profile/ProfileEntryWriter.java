package at.swimmesberger.bo2.profile;

import java.io.IOException;
import java.util.Collection;

public interface ProfileEntryWriter extends AutoCloseable {
    void begin(long entryCount) throws IOException;

    void write(ProfileEntry<?> entry) throws IOException;

    void end() throws IOException;

    default void write(Collection<ProfileEntry<?>> entries) throws IOException {
        this.begin(entries.size());
        for(ProfileEntry<?> entry : entries) {
            this.write(entry);
        }
        this.end();
    }

    default void write(ProfileEntryIterator entryIterator) throws IOException {
        this.begin(entryIterator.getEntryCount());
        while(entryIterator.hasNext()) {
            this.write(entryIterator.next());
        }
        this.end();
    }

    @Override
    void close() throws IOException;
}

package at.swimmesberger.bo2.profile;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

public interface ProfileEntryReader extends AutoCloseable {
    Stream<ProfileEntry<?>> streamEntries() throws IOException;

    Spliterator<ProfileEntry<?>> spliterateEntries() throws IOException;

    ProfileEntryIterator iterateEntries() throws IOException;

    List<ProfileEntry<?>> readEntries() throws IOException;

    @Override
    void close() throws IOException;

}

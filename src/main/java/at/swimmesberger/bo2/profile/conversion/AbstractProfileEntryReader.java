package at.swimmesberger.bo2.profile.conversion;

import at.swimmesberger.bo2.profile.ProfileEntry;
import at.swimmesberger.bo2.profile.ProfileEntryIterator;
import at.swimmesberger.bo2.profile.ProfileEntryReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractProfileEntryReader implements ProfileEntryReader {
    @Override
    public Stream<ProfileEntry<?>> streamEntries() throws IOException {
        return this.toStream(this.spliterateEntries());
    }

    @Override
    public Spliterator<ProfileEntry<?>> spliterateEntries() throws IOException {
        return this.toSpliterator(this.iterateEntries());
    }

    @Override
    public List<ProfileEntry<?>> readEntries() throws IOException {
        ProfileEntryIterator iterator = this.iterateEntries();
        return this.toStream(iterator).collect(Collectors.toCollection(() -> new ArrayList<>((int)iterator.getEntryCount())));
    }

    private Stream<ProfileEntry<?>> toStream(ProfileEntryIterator iterator) {
        return toStream(this.toSpliterator(iterator));
    }

    private Stream<ProfileEntry<?>> toStream(Spliterator<ProfileEntry<?>> spliterator) {
        return StreamSupport.stream(spliterator, false);
    }

    private Spliterator<ProfileEntry<?>> toSpliterator(ProfileEntryIterator iterator) {
        return Spliterators.spliterator(iterator, iterator.getEntryCount(), Spliterator.SIZED | Spliterator.SORTED);
    }
}

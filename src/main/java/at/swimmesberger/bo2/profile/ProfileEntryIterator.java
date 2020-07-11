package at.swimmesberger.bo2.profile;

import java.util.Iterator;

public interface ProfileEntryIterator extends Iterator<ProfileEntry<?>> {
    long getEntryCount();
}

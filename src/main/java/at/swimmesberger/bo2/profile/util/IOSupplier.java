package at.swimmesberger.bo2.profile.util;

import java.io.IOException;

public interface IOSupplier<T> {
    T get() throws IOException;
}

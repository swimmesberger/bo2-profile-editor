package at.swimmesberger.bo2.profile;

import java.io.IOException;
import java.io.InputStream;

public class ProfileReaderFactory {
    public ProfileEntryReader createReader(InputStream inputStream, ContainerFormat format) throws IOException {
        switch (format) {
            case JSON:
                return new JsonProfileEntryReader(inputStream);
            case BINARY:
                return new BinaryProfileEntryReader(inputStream);
            default:
                throw new IllegalArgumentException("Unsupported input format " + format);
        }
    }
}

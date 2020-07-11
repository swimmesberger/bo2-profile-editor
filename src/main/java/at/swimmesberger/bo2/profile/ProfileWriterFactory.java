package at.swimmesberger.bo2.profile;

import java.io.IOException;
import java.io.OutputStream;

public class ProfileWriterFactory {
    public ProfileEntryWriter createWriter(OutputStream out, ContainerFormat format) throws IOException {
        switch (format) {
            case JSON:
                return new JsonProfileEntryWriter(out);
            case TABLE:
                return new TableProfileEntryWriter(out);
            case BINARY:
                return new BinaryProfileEntryWriter(out);
            default:
                throw new IllegalArgumentException("Unsupported input format " + format);
        }
    }
}

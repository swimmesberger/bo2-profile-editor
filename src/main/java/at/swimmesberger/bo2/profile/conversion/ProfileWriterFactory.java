package at.swimmesberger.bo2.profile.conversion;

import at.swimmesberger.bo2.profile.EntriesContainerFormat;
import at.swimmesberger.bo2.profile.ProfileEntryWriter;

import java.io.IOException;
import java.io.OutputStream;

public class ProfileWriterFactory {
    public ProfileEntryWriter createWriter(OutputStream out, EntriesContainerFormat format) throws IOException {
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

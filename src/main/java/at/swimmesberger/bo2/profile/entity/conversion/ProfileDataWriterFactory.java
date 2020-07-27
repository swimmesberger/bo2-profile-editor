package at.swimmesberger.bo2.profile.entity.conversion;

import java.io.IOException;
import java.io.OutputStream;

public class ProfileDataWriterFactory {
    public ProfileDataWriter createWriter(OutputStream out, ProfileDataContainerFormat format) throws IOException {
        switch (format) {
            case JSON:
                return new JsonProfileDataWriter(out);
            case TABLE:
                return new TableProfileDataWriter(out);
            default:
                throw new IllegalArgumentException("Unsupported input format " + format);
        }
    }
}

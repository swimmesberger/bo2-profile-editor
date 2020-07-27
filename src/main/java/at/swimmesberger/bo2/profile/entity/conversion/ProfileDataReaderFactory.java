package at.swimmesberger.bo2.profile.entity.conversion;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDataReaderFactory {
    public ProfileDataReader createReader(InputStream inputStream, ProfileDataContainerFormat format) throws IOException {
        switch (format) {
            case JSON:
                return new JsonProfileDataReader(inputStream);
            default:
                throw new IllegalArgumentException("Unsupported input format " + format);
        }
    }
}

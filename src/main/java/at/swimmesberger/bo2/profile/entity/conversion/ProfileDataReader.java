package at.swimmesberger.bo2.profile.entity.conversion;

import at.swimmesberger.bo2.profile.entity.ProfileData;

import java.io.IOException;

public interface ProfileDataReader extends AutoCloseable {
    ProfileData readData() throws IOException;

    @Override
    void close() throws IOException;
}

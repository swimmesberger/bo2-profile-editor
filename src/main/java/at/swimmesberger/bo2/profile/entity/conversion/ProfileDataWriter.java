package at.swimmesberger.bo2.profile.entity.conversion;

import at.swimmesberger.bo2.profile.entity.ProfileData;

import java.io.IOException;

public interface ProfileDataWriter extends AutoCloseable {
    void writeData(ProfileData profileData) throws IOException;

    @Override
    void close() throws IOException;
}

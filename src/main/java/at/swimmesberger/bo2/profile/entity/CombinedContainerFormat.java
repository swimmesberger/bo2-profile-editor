package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.EntriesContainerFormat;
import at.swimmesberger.bo2.profile.entity.conversion.ProfileDataContainerFormat;

public enum CombinedContainerFormat {
    // data is still LZO compressed
    COMPRESSED_LZO,
    // data is in raw uncompressed state
    BINARY,
    // data in uncompressed state converted to JSON
    JSON,
    // ASCII table format for entries (best for console output)
    TABLE,
    // data in uncompressed decoded state converted to JSON
    JSON_DATA,
    // data in uncompressed decoded state converted to ASCII table format (best for console output)
    TABLE_DATA;

    public static CombinedContainerFormat from(EntriesContainerFormat entriesContainerFormat) {
        switch (entriesContainerFormat) {
            case COMPRESSED_LZO:
                return CombinedContainerFormat.COMPRESSED_LZO;
            case BINARY:
                return CombinedContainerFormat.BINARY;
            case JSON:
                return CombinedContainerFormat.JSON;
            case TABLE:
                return CombinedContainerFormat.TABLE;
            default:
                return null;
        }
    }

    public static CombinedContainerFormat from(ProfileDataContainerFormat dataContainerFormat) {
        switch (dataContainerFormat) {
            case JSON:
                return CombinedContainerFormat.JSON_DATA;
            case TABLE:
                return CombinedContainerFormat.TABLE_DATA;
            default:
                return null;
        }
    }

    public boolean isEntriesContainerFormat() {
        if (this == COMPRESSED_LZO || this == BINARY || this == JSON || this == TABLE) {
            return true;
        }
        return false;
    }

    public EntriesContainerFormat getEntriesContainerFormat() {
        switch (this) {
            case COMPRESSED_LZO:
                return EntriesContainerFormat.COMPRESSED_LZO;
            case BINARY:
                return EntriesContainerFormat.BINARY;
            case JSON:
                return EntriesContainerFormat.JSON;
            case TABLE:
                return EntriesContainerFormat.TABLE;
            default:
                return null;
        }
    }

    public boolean isDataContainerFormat() {
        if (this == JSON_DATA || this == TABLE_DATA) {
            return true;
        }
        return false;
    }

    public ProfileDataContainerFormat getDataContainerFormat() {
        switch (this) {
            case JSON_DATA:
                return ProfileDataContainerFormat.JSON;
            case TABLE_DATA:
                return ProfileDataContainerFormat.TABLE;
            default:
                return null;
        }
    }
}

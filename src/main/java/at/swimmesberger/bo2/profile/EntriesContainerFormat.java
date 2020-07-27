package at.swimmesberger.bo2.profile;

public enum EntriesContainerFormat {
    // data is still LZO compressed
    COMPRESSED_LZO,
    // data is in raw uncompressed state
    BINARY,
    // data in uncompressed state converted to JSON
    JSON,
    // ASCII table format (best for console output)
    TABLE
}

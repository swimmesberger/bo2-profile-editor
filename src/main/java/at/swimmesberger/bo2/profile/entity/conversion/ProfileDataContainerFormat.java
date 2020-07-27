package at.swimmesberger.bo2.profile.entity.conversion;

public enum ProfileDataContainerFormat {
    // data in uncompressed decoded state converted to JSON
    JSON,
    // data in uncompressed decoded state converted to ASCII table format (best for console output)
    TABLE;
}

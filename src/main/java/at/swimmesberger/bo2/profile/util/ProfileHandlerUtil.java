package at.swimmesberger.bo2.profile.util;

import at.swimmesberger.bo2.profile.EntriesContainerFormat;
import at.swimmesberger.bo2.profile.entity.CombinedContainerFormat;
import at.swimmesberger.bo2.profile.entity.conversion.ProfileDataContainerFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class ProfileHandlerUtil {
    public static EntriesContainerFormat detectEntriesFormat(Path file) {
        String extension = FilenameUtils.getExtension(file.getFileName().toString());
        extension = extension.toLowerCase(Locale.ENGLISH);
        switch (extension) {
            case "uncompressed":
                return EntriesContainerFormat.BINARY;
            case "json":
                return EntriesContainerFormat.JSON;
            case "bin":
                return EntriesContainerFormat.COMPRESSED_LZO;
            case "table":
                return EntriesContainerFormat.TABLE;
            default:
                throw new IllegalArgumentException("Invalid file extension " + extension);
        }
    }

    public static CombinedContainerFormat detectFormat(Path file) {
        try {
            ProfileDataContainerFormat profileDataContainerFormat = detectDataFormat(file);
            return CombinedContainerFormat.from(profileDataContainerFormat);
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        return CombinedContainerFormat.from(detectEntriesFormat(file));
    }

    public static ProfileDataContainerFormat detectDataFormat(Path file) {
        String extension = FilenameUtils.getExtension(file.getFileName().toString());
        extension = extension.toLowerCase(Locale.ENGLISH);
        switch (extension) {
            case "json":
                return ProfileDataContainerFormat.JSON;
            case "table":
                return ProfileDataContainerFormat.TABLE;
            default:
                throw new IllegalArgumentException("Invalid file extension " + extension);
        }
    }

    public static OutputStreamSupplier.CloseableOutputStreamSupplier newFileOutputSupplier(Path file) throws IOException {
        return OutputStreamSupplier.CloseableOutputStreamSupplier.create(() -> newFileOutputStream(file));
    }

    public static InputStream newFileInputStream(Path file) throws IOException {
        return new BufferedInputStream(Files.newInputStream(file));
    }

    public static OutputStream newFileOutputStream(Path file) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
    }
}

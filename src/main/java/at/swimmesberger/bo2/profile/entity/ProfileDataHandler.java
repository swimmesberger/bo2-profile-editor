package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.ContainerFormat;
import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntryDataHandler;
import at.swimmesberger.bo2.profile.util.OutputStreamSupplier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ProfileDataHandler {
    private final ProfileEntryDataHandler entryDataHandler;
    private final ProfileDataConverter profileDataConverter;

    public ProfileDataHandler() {
        this.entryDataHandler = new ProfileEntryDataHandler();
        this.profileDataConverter = new ProfileDataConverter();
    }

    public String getValue(Path inputFile, ProfileDataValueType type) throws IOException {
        String value;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            this.getValue(inputFile, byteArrayOutputStream, type);
            value = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        }
        return value;
    }

    public void getValue(Path inputFile, OutputStream outputStream, ProfileDataValueType type) throws IOException {
        ProfileEntries entries = this.entryDataHandler.readEntries(inputFile);
        ProfileData data = this.profileDataConverter.decodeEntries(entries);
        Object value = data.getValue(type);
        String stringValue = String.valueOf(value);
        outputStream.write(stringValue.getBytes(StandardCharsets.UTF_8));
    }

    public void setValue(Path inputFile, Path outputFile, ProfileDataValueType type, String value) throws IOException {
        ContainerFormat outputFormat = ProfileEntryDataHandler.detectFormat(outputFile);
        try (OutputStreamSupplier.CloseableOutputStreamSupplier outputStreamSupplier = OutputStreamSupplier.newFileOutputStream(outputFile)) {
            this.setValue(inputFile, outputStreamSupplier, outputFormat, type, value);
        }
    }

    public void setValue(Path inputFile, OutputStreamSupplier outputStreamSupplier, ContainerFormat outputFormat, ProfileDataValueType type, String value) throws IOException {
        ProfileEntries entries = this.entryDataHandler.readEntries(inputFile);
        ProfileData data = this.profileDataConverter.decodeEntries(entries);
        ProfileData modifiedData = data.setValue(type, value);
        ProfileEntries modifiedEntries = this.profileDataConverter.encodeEntries(modifiedData, entries);
        this.entryDataHandler.writeEntries(modifiedEntries, outputStreamSupplier, outputFormat);
    }
}

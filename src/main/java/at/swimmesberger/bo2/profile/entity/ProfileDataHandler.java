package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.EntriesContainerFormat;
import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntryDataHandler;
import at.swimmesberger.bo2.profile.entity.conversion.*;
import at.swimmesberger.bo2.profile.util.IOUtils;
import at.swimmesberger.bo2.profile.util.OutputStreamSupplier;
import at.swimmesberger.bo2.profile.util.ProfileHandlerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ProfileDataHandler {
    private final ProfileEntryDataHandler entryDataHandler;
    private final ProfileDataConverter profileDataConverter;
    private final ProfileDataReaderFactory dataReaderFactory;
    private final ProfileDataWriterFactory dataWriterFactory;

    public ProfileDataHandler() {
        this.entryDataHandler = new ProfileEntryDataHandler();
        this.profileDataConverter = new ProfileDataConverter();
        this.dataReaderFactory = new ProfileDataReaderFactory();
        this.dataWriterFactory = new ProfileDataWriterFactory();
    }

    public void convertEntries(Path inputFile, CombinedContainerFormat inputFormat, Path outputFile, CombinedContainerFormat outputFormat) throws IOException {
        if (outputFormat == null) {
            outputFormat = ProfileHandlerUtil.detectFormat(outputFile);
        }
        if (inputFormat == null) {
            inputFormat = ProfileHandlerUtil.detectFormat(inputFile);
        }
        if (inputFormat.equals(outputFormat) && inputFile.equals(outputFile)) {
            return;
        }
        try (OutputStreamSupplier.CloseableOutputStreamSupplier out = ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            this.convertEntries(inputFile, inputFormat, out, outputFormat);
        }
    }

    public void convertEntries(Path inputFile, CombinedContainerFormat inputFormat, OutputStream outputStream, CombinedContainerFormat outputFormat) throws IOException {
        this.convertEntries(inputFile, inputFormat, () -> outputStream, outputFormat);
    }

    public void convertEntries(Path inputFile, CombinedContainerFormat inputFormat, OutputStreamSupplier outputStreamSupplier, CombinedContainerFormat outputFormat) throws IOException {
        if (inputFormat == null) {
            inputFormat = ProfileHandlerUtil.detectFormat(inputFile);
        }
        try (InputStream in = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            this.convertEntries(in, inputFormat, outputStreamSupplier, outputFormat);
        }
    }

    public void convertEntries(InputStream inputStream, @NotNull CombinedContainerFormat inputFormat, OutputStreamSupplier outputStream, @NotNull CombinedContainerFormat outputFormat) throws IOException {
        if (inputFormat.equals(outputFormat)) {
            IOUtils.copy(inputStream, outputStream.get());
            return;
        }
        if (inputFormat.isEntriesContainerFormat() && outputFormat.isEntriesContainerFormat()) {
            this.entryDataHandler.convertEntries(inputStream, inputFormat.getEntriesContainerFormat(), outputStream, outputFormat.getEntriesContainerFormat());
            return;
        }
        ProfileData profileData;
        ProfileEntries entries = null;
        if (inputFormat.isEntriesContainerFormat()) {
            entries = this.entryDataHandler.readEntries(inputStream, inputFormat.getEntriesContainerFormat());
            profileData = this.profileDataConverter.decodeEntries(entries);
        } else {
            profileData = this.readEntries(inputStream, inputFormat.getDataContainerFormat());
        }
        if (outputFormat.isEntriesContainerFormat()) {
            if (entries == null) {
                entries = this.profileDataConverter.encodeEntries(profileData);
            }
            this.entryDataHandler.writeEntries(entries, outputStream, outputFormat.getEntriesContainerFormat());
        } else {
            this.writeEntries(profileData, outputStream, outputFormat.getDataContainerFormat());
        }
    }

    public ProfileData readEntries(InputStream inputStream, ProfileDataContainerFormat inputFormat) throws IOException {
        try (ProfileDataReader entryReader = this.dataReaderFactory.createReader(inputStream, inputFormat)) {
            return entryReader.readData();
        }
    }

    public void writeEntries(ProfileData data, OutputStreamSupplier outputStreamSupplier, ProfileDataContainerFormat outputFormat) throws IOException {
        OutputStream outputStream = outputStreamSupplier.get();
        try (ProfileDataWriter entryWriter = this.dataWriterFactory.createWriter(outputStream, outputFormat)) {
            entryWriter.writeData(data);
        }
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
        EntriesContainerFormat outputFormat = ProfileHandlerUtil.detectEntriesFormat(outputFile);
        try (OutputStreamSupplier.CloseableOutputStreamSupplier outputStreamSupplier = ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            this.setValue(inputFile, outputStreamSupplier, outputFormat, type, value);
        }
    }

    public void setValue(Path inputFile, OutputStreamSupplier outputStreamSupplier, EntriesContainerFormat outputFormat, ProfileDataValueType type, String value) throws IOException {
        ProfileEntries entries = this.entryDataHandler.readEntries(inputFile);
        ProfileData data = this.profileDataConverter.decodeEntries(entries);
        ProfileData modifiedData = data.setValue(type, value);
        ProfileEntries modifiedEntries = this.profileDataConverter.encodeEntries(modifiedData, entries);
        this.entryDataHandler.writeEntries(modifiedEntries, outputStreamSupplier, outputFormat);
    }
}

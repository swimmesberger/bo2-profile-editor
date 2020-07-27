package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.conversion.ProfileReaderFactory;
import at.swimmesberger.bo2.profile.conversion.ProfileWriterFactory;
import at.swimmesberger.bo2.profile.util.FilenameUtils;
import at.swimmesberger.bo2.profile.util.IOUtils;
import at.swimmesberger.bo2.profile.util.OutputStreamSupplier;
import at.swimmesberger.bo2.profile.util.ProfileHandlerUtil;
import org.anarres.lzo.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProfileEntryDataHandler {
    protected static final String DECOMPRESSED_FILE_EXT = ".decompressed";
    private static final int BUFFER_SIZE = 8192;

    private final ProfileWriterFactory profileWriterFactory;
    private final ProfileReaderFactory profileReaderFactory;

    public ProfileEntryDataHandler() {
        this.profileWriterFactory = new ProfileWriterFactory();
        this.profileReaderFactory = new ProfileReaderFactory();
    }

    public ProfileEntries readEntries(Path inputFile) throws IOException {
        return this.readEntries(inputFile, null);
    }

    public ProfileEntries readEntries(Path inputFile, EntriesContainerFormat inputFormat) throws IOException {
        if (inputFormat == null) {
            inputFormat = ProfileHandlerUtil.detectEntriesFormat(inputFile);
        }
        try (InputStream in = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            return this.readEntries(in, inputFormat);
        }
    }

    public ProfileEntries readEntries(InputStream inputStream) throws IOException {
        return this.readEntries(inputStream, null);
    }

    public ProfileEntries readEntries(InputStream inputStream, EntriesContainerFormat inputFormat) throws IOException {
        if (inputFormat == EntriesContainerFormat.COMPRESSED_LZO) {
            inputStream = this.uncompress(inputStream);
            inputFormat = EntriesContainerFormat.BINARY;
        }
        try (ProfileEntryReader entryReader = this.profileReaderFactory.createReader(inputStream, inputFormat)) {
            return ProfileEntries.from(entryReader.readEntries());
        }
    }

    public void writeEntries(ProfileEntries entries, Path outputFile) throws IOException {
        this.writeEntries(entries, outputFile, null);
    }

    public void writeEntries(ProfileEntries entries, Path outputFile, EntriesContainerFormat outputFormat) throws IOException {
        if (outputFormat == null) {
            outputFormat = ProfileHandlerUtil.detectEntriesFormat(outputFile);
        }
        try (OutputStreamSupplier.CloseableOutputStreamSupplier out = ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            this.writeEntries(entries, out, outputFormat);
        }
    }

    public void writeEntries(ProfileEntries entries, OutputStreamSupplier outputStream) throws IOException {
        this.writeEntries(entries, outputStream, null);
    }

    public void writeEntries(ProfileEntries entries, OutputStreamSupplier outputStream, EntriesContainerFormat outputFormat) throws IOException {
        if (outputFormat == null) {
            outputFormat = EntriesContainerFormat.COMPRESSED_LZO;
        }
        if (outputFormat == EntriesContainerFormat.COMPRESSED_LZO) {
            this.writeCompressed(entries, outputStream, EntriesContainerFormat.BINARY);
        } else {
            this.writeEntriesImpl(entries, outputStream, outputFormat);
        }
    }

    private void writeCompressed(ProfileEntries entries, OutputStreamSupplier outputStream, EntriesContainerFormat outputFormat) throws IOException {
        byte[] uncompressedData;
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
            this.writeEntriesImpl(entries, bOut, outputFormat);
            uncompressedData = bOut.toByteArray();
        }
        this.compress(uncompressedData, outputStream);
    }

    private void writeEntriesImpl(ProfileEntries entries, OutputStream outputStream, EntriesContainerFormat outputFormat) throws IOException {
        this.writeEntriesImpl(entries, () -> outputStream, outputFormat);
    }

    private void writeEntriesImpl(ProfileEntries entries, OutputStreamSupplier outputStreamSupplier, EntriesContainerFormat outputFormat) throws IOException {
        OutputStream outputStream = outputStreamSupplier.get();
        try (ProfileEntryWriter entryWriter = this.profileWriterFactory.createWriter(outputStream, outputFormat)) {
            entryWriter.write(entries.getEntries());
        }
    }

    public void convertEntries(Path inputFile, Path outputFile) throws IOException {
        this.convertEntries(inputFile, null, outputFile, null);
    }

    public void convertEntries(Path inputFile, EntriesContainerFormat inputFormat, Path outputFile, EntriesContainerFormat outputFormat) throws IOException {
        if (outputFormat == null) {
            outputFormat = ProfileHandlerUtil.detectEntriesFormat(outputFile);
        }
        if (inputFormat == null) {
            inputFormat = ProfileHandlerUtil.detectEntriesFormat(inputFile);
        }
        if (inputFormat.equals(outputFormat) && inputFile.equals(outputFile)) {
            return;
        }
        try (OutputStreamSupplier.CloseableOutputStreamSupplier out = ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            this.convertEntries(inputFile, inputFormat, out, outputFormat);
        }
    }

    public void convertEntries(Path inputFile, EntriesContainerFormat inputFormat, OutputStream outputStream, EntriesContainerFormat outputFormat) throws IOException {
        this.convertEntries(inputFile, inputFormat, () -> outputStream, outputFormat);
    }

    public void convertEntries(Path inputFile, EntriesContainerFormat inputFormat, OutputStreamSupplier outputStreamSupplier, EntriesContainerFormat outputFormat) throws IOException {
        if (inputFormat == null) {
            inputFormat = ProfileHandlerUtil.detectEntriesFormat(inputFile);
        }
        try (InputStream in = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            this.convertEntries(in, inputFormat, outputStreamSupplier, outputFormat);
        }
    }

    public void convertEntries(InputStream inputStream, @NotNull EntriesContainerFormat inputFormat, OutputStreamSupplier outputStream, @NotNull EntriesContainerFormat outputFormat) throws IOException {
        if (inputFormat.equals(outputFormat)) {
            IOUtils.copy(inputStream, outputStream.get());
            return;
        }
        if (inputFormat == EntriesContainerFormat.COMPRESSED_LZO && outputFormat == EntriesContainerFormat.BINARY) {
            this.decompress(inputStream, outputStream);
            return;
        }
        if (inputFormat == EntriesContainerFormat.BINARY && outputFormat == EntriesContainerFormat.COMPRESSED_LZO) {
            this.compress(inputStream, outputStream);
            return;
        }

        InputStream uncompressedStream;
        if (inputFormat == EntriesContainerFormat.COMPRESSED_LZO) {
            uncompressedStream = this.uncompress(inputStream);
            inputFormat = EntriesContainerFormat.BINARY;
        } else {
            uncompressedStream = inputStream;
        }
        if (outputFormat == EntriesContainerFormat.COMPRESSED_LZO) {
            byte[] uncompressedData;
            try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
                this.convertEntriesImpl(uncompressedStream, inputFormat, bOut, outputFormat);
                uncompressedData = bOut.toByteArray();
            }
            this.compress(uncompressedData, outputStream);
        } else {
            this.convertEntriesImpl(uncompressedStream, inputFormat, outputStream, outputFormat);
        }
    }

    private InputStream uncompress(InputStream inputStream) throws IOException {
        return new ByteArrayInputStream(this.decompressInMemory(inputStream));
    }

    private void convertEntriesImpl(InputStream inputStream, EntriesContainerFormat inputFormat, OutputStream outputStream, EntriesContainerFormat outputFormat) throws IOException {
        this.convertEntriesImpl(inputStream, inputFormat, () -> outputStream, outputFormat);
    }

    private void convertEntriesImpl(InputStream inputStream, EntriesContainerFormat inputFormat, OutputStreamSupplier outputStreamSupplier, EntriesContainerFormat outputFormat) throws IOException {
        try (ProfileEntryReader entryReader = this.profileReaderFactory.createReader(inputStream, inputFormat)) {
            ProfileEntryIterator iterator = entryReader.iterateEntries();
            OutputStream outputStream = outputStreamSupplier.get();
            try (ProfileEntryWriter entryWriter = this.profileWriterFactory.createWriter(outputStream, outputFormat)) {
                entryWriter.write(iterator);
            }
        }
    }

    public byte[] compressInMemory(Path inputFile) throws IOException {
        try (InputStream inputStream = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            return this.compressInMemory(inputStream);
        }
    }

    public byte[] compressInMemory(InputStream inputStream) throws IOException {
        byte[] data;
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
            this.compress(inputStream, bOut);
            data = bOut.toByteArray();
        }
        return data;
    }

    public void compress(Path inputFile) throws IOException {
        this.compress(inputFile, (Path) null);
    }

    public void compress(Path inputFile, Path outputFile) throws IOException {
        if (outputFile == null) {
            String baseName = FilenameUtils.getBaseName(inputFile.getFileName().toString());
            outputFile = inputFile.getParent().resolve(baseName);
        }
        try (OutputStreamSupplier.CloseableOutputStreamSupplier outputStream = ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            this.compress(inputFile, outputStream);
        }
    }

    public void compress(Path inputFile, OutputStreamSupplier outputStream) throws IOException {
        try (InputStream inputStream = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            this.compress(inputStream, outputStream);
        }
    }

    public void compress(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.compress(inputStream, () -> outputStream);
    }

    public void compress(InputStream inputStream, OutputStreamSupplier outputStream) throws IOException {
        byte[] inputData = this.readBytes(inputStream);
        this.compress(inputData, outputStream);
    }

    public void compress(byte[] inputData, OutputStreamSupplier outputStreamSupplier) throws IOException {
        int inputDataSize = inputData.length;
        int expectedOutBytes = inputData.length + (inputData.length / 16) + 64 + 3;
        byte[] outData;
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(expectedOutBytes + 4); DataOutputStream out = new DataOutputStream(bout)) {
            byte[] compressedBytes = new byte[expectedOutBytes];
            int actualCompressedBytesSize = lzoCompress(inputData, compressedBytes);
            out.writeInt(inputDataSize);
            out.write(compressedBytes, 0, actualCompressedBytesSize);
            out.flush();
            outData = bout.toByteArray();
        }
        byte[] sha1Hash = this.sha1Hash(outData);
        OutputStream outputStream = outputStreamSupplier.get();
        outputStream.write(sha1Hash);
        outputStream.write(outData, 0, outData.length);
    }

    public void decompress(Path inputFile) throws IOException {
        this.decompress(inputFile, (Path) null);
    }

    public void decompress(Path inputFile, Path outputFile) throws IOException {
        if (outputFile == null) {
            outputFile = inputFile.getParent().resolve(inputFile.getFileName().toString() + DECOMPRESSED_FILE_EXT);
        }
        try (OutputStreamSupplier.CloseableOutputStreamSupplier outputStream = ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            this.decompress(inputFile, outputStream);
        }
    }

    public void decompress(Path inputFile, OutputStream outputStream) throws IOException {
        this.decompress(inputFile, () -> outputStream);
    }

    public void decompress(Path inputFile, OutputStreamSupplier outputStream) throws IOException {
        try (InputStream inputStream = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            this.decompress(inputStream, outputStream);
        }
    }

    public byte[] decompressInMemory(Path inputFile) throws IOException {
        try (InputStream inputStream = ProfileHandlerUtil.newFileInputStream(inputFile)) {
            return this.decompressInMemory(inputStream);
        }
    }

    public byte[] decompressInMemory(InputStream inputStream) throws IOException {
        byte[] data;
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
            this.decompress(inputStream, bOut);
            data = bOut.toByteArray();
        }
        return data;
    }

    public void decompress(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.decompress(inputStream, () -> outputStream);
    }

    public void decompress(InputStream inputStream, OutputStreamSupplier output) throws IOException {
        DataInputStream in = new DataInputStream(inputStream);
        byte[] sha1Hash = new byte[20];
        int readBytes = in.read(sha1Hash);
        if (readBytes != 20) {
            throw new IllegalStateException("No sha1 hash header found!");
        }
        int uncompressedSize = (int) Integer.toUnsignedLong(in.readInt());
        byte[] compressedData = this.readBytes(in);
        int compressedDataLength = compressedData.length;
        //TODO: check sha1Hash
        byte[] uncompressedData = new byte[uncompressedSize];

        this.lzoUncompress(compressedData, compressedDataLength, uncompressedData, uncompressedSize);
        this.writeBytes(uncompressedData, output.get());
    }

    private byte[] sha1Hash(byte[] data) {
        return this.sha1Hash(data, 0, data.length);
    }

    private byte[] sha1Hash(byte[] data, int offset, int length) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        digest.update(data, offset, length);
        return digest.digest();
    }

    private int lzoCompress(byte[] uncompressedData, byte[] compressedData) {
        return this.lzoCompress(uncompressedData, uncompressedData.length, compressedData, compressedData.length);
    }

    private int lzoCompress(byte[] uncompressedData, int uncompressedSize, byte[] compressedData, int compressedSize) {
        LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
        LzoCompressor compressor = LzoLibrary.getInstance().newCompressor(algorithm, null);
        lzo_uintp outputBufferLen = new lzo_uintp();
        outputBufferLen.value = compressedSize;
        int code = compressor.compress(uncompressedData, 0, uncompressedSize, compressedData, 0, outputBufferLen);
        if (code != LzoTransformer.LZO_E_OK) {
            throw new IllegalArgumentException(compressor.toErrorString(code));
        }
        return outputBufferLen.value;
    }

    private void lzoUncompress(byte[] compressedData, int compressedLength, byte[] uncompressedData, int uncompressedSize) {
        LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
        LzoDecompressor decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, LzoConstraint.SAFETY);
        lzo_uintp outputBufferLen = new lzo_uintp();
        outputBufferLen.value = uncompressedSize;
        int code = decompressor.decompress(compressedData, 0, compressedLength, uncompressedData, 0, outputBufferLen);
        if (code != LzoTransformer.LZO_E_OK) {
            throw new IllegalArgumentException(decompressor.toErrorString(code));
        }
    }

    private void writeBytes(byte[] data, OutputStream out) throws IOException {
        int len = data.length;
        int rem = len;
        while (rem > 0) {
            int n = Math.min(rem, BUFFER_SIZE);
            out.write(data, (len - rem), n);
            rem -= n;
        }
    }

    private byte[] readBytes(InputStream in) throws IOException {
        return IOUtils.toByteArray(in);
    }
}

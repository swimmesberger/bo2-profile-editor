package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.util.FilenameUtils;
import at.swimmesberger.bo2.profile.util.IOUtils;
import org.anarres.lzo.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

// offset 6EF (1755) 1 byte = Golden Keys
public class ProfileDataHandler {
    protected static final String DECOMPRESSED_FILE_EXT = ".decompressed";
    private static final int BUFFER_SIZE = 8192;

    private final ProfileWriterFactory profileWriterFactory;
    private final ProfileReaderFactory profileReaderFactory;

    public ProfileDataHandler() {
        this.profileWriterFactory = new ProfileWriterFactory();
        this.profileReaderFactory = new ProfileReaderFactory();
    }

    public void printEntries(Path inputFile, Path outputFile) throws IOException {
        this.printEntries(inputFile, null, outputFile, null);
    }

    public void printEntries(Path inputFile, ContainerFormat inputFormat, Path outputFile, ContainerFormat outputFormat) throws IOException {
        if (outputFormat == null) {
            outputFormat = this.detectFormat(outputFile);
        }
        this.printEntries(inputFile, inputFormat, newFileOutputStream(outputFile), outputFormat);
    }

    public void printEntries(Path inputFile, ContainerFormat inputFormat, OutputStream outputStream, ContainerFormat outputFormat) throws IOException {
        if (inputFormat == null) {
            inputFormat = this.detectFormat(inputFile);
        }
        this.printEntries(newFileInputStream(inputFile), inputFormat, outputStream, outputFormat);
    }

    public void printEntries(InputStream inputStream, ContainerFormat inputFormat, OutputStream outputStream, ContainerFormat outputFormat) throws IOException {
        InputStream uncompressedStream;
        if (inputFormat == ContainerFormat.COMPRESSED_LZO) {
            uncompressedStream = new ByteArrayInputStream(this.decompressInMemory(inputStream));
            inputFormat = ContainerFormat.BINARY;
        } else {
            uncompressedStream = inputStream;
        }
        if (outputFormat == ContainerFormat.COMPRESSED_LZO) {
            byte[] uncompressedData;
            try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
                this.printEntriesImpl(uncompressedStream, inputFormat, bOut, outputFormat);
                uncompressedData = bOut.toByteArray();
            }
            this.compress(uncompressedData, outputStream);
        } else {
            this.printEntriesImpl(uncompressedStream, inputFormat, outputStream, outputFormat);
        }
    }

    private void printEntriesImpl(InputStream inputStream, ContainerFormat inputFormat, OutputStream outputStream, ContainerFormat outputFormat) throws IOException {
        try (ProfileEntryWriter entryWriter = this.profileWriterFactory.createWriter(outputStream, outputFormat)) {
            try (ProfileEntryReader entryReader = this.profileReaderFactory.createReader(inputStream, inputFormat)) {
                entryWriter.write(entryReader.iterateEntries());
            }
        }
    }

    public void compress(Path inputFile) throws IOException {
        this.compress(inputFile, (Path) null);
    }

    public void compress(Path inputFile, Path outputFile) throws IOException {
        if (outputFile == null) {
            String baseName = FilenameUtils.getBaseName(inputFile.getFileName().toString());
            outputFile = inputFile.getParent().resolve(baseName);
        }
        try (OutputStream outputStream = newFileOutputStream(outputFile)) {
            this.compress(inputFile, outputStream);
        }
    }

    public void compress(Path inputFile, OutputStream outputStream) throws IOException {
        try (InputStream inputStream = newFileInputStream(inputFile)) {
            this.compress(inputStream, outputStream);
        }
    }


    public void compress(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] inputData = this.readBytes(inputStream);
        this.compress(inputData, outputStream);
    }

    public void compress(byte[] inputData, OutputStream outputStream) throws IOException {
        int inputDataSize = inputData.length;
        int expectedOutBytes = inputData.length + (inputData.length / 16) + 64 + 3;
        byte[] outData;
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(expectedOutBytes + 4); DataOutputStream out = new DataOutputStream(bout)) {
            byte[] compressedBytes = new byte[expectedOutBytes];
            int actualCompressedBytesSize = lzoCompress(inputData, inputData.length, compressedBytes, compressedBytes.length);
            out.writeInt(inputDataSize);
            out.write(compressedBytes, 0, actualCompressedBytesSize);
            out.flush();
            outData = bout.toByteArray();
        }
        byte[] sha1Hash = this.sha1Hash(outData);
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
        try (OutputStream outputStream = Files.newOutputStream(outputFile)) {
            this.decompress(inputFile, outputStream);
        }
    }

    public void decompress(Path inputFile, OutputStream outputStream) throws IOException {
        try (InputStream inputStream = this.newFileInputStream(inputFile)) {
            this.decompress(inputStream, outputStream);
        }
    }

    public byte[] decompressInMemory(Path inputFile) throws IOException {
        try (InputStream inputStream = this.newFileInputStream(inputFile)) {
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

    public void decompress(InputStream inputStream, OutputStream output) throws IOException {
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
        this.writeBytes(uncompressedData, output);
    }

    private byte[] sha1Hash(byte[] data) throws IOException {
        return this.sha1Hash(data, 0, data.length);
    }

    private byte[] sha1Hash(byte[] data, int offset, int length) throws IOException {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        digest.update(data, offset, length);
        return digest.digest();
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

    private InputStream newFileInputStream(Path file) throws IOException {
        return new BufferedInputStream(Files.newInputStream(file));
    }

    private OutputStream newFileOutputStream(Path file) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(file));
    }

    private ContainerFormat detectFormat(Path inputFile) {
        String extension = FilenameUtils.getExtension(inputFile.getFileName().toString());
        extension = extension.toLowerCase(Locale.ENGLISH);
        if (extension.equals("uncompressed")) {
            return ContainerFormat.BINARY;
        } else if (extension.equals("json")) {
            return ContainerFormat.JSON;
        } else if (extension.equals("bin")) {
            return ContainerFormat.COMPRESSED_LZO;
        } else if (extension.equals("table")) {
            return ContainerFormat.TABLE;
        } else {
            throw new IllegalArgumentException("Invalid input file extension " + extension);
        }
    }
}

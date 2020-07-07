package at.swimmesberger.bo2.profile;

import org.anarres.lzo.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// offset 6EF (1755) 1 byte = Golden Keys
public class ProfileDataHandler {
    protected static final String DECOMPRESSED_FILE_EXT = ".decompressed";
    private static final int BUFFER_SIZE = 8192;

    public void compress(Path inputFile) throws IOException {
        this.compress(inputFile, (Path)null);
    }

    public void compress(Path inputFile, Path outputFile) throws IOException {
        if (outputFile == null) {
            String baseName = FilenameUtils.getBaseName(inputFile.getFileName().toString());
            outputFile = inputFile.getParent().resolve(baseName);
        }
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(outputFile))) {
            this.compress(inputFile, outputStream);
        }
    }

    public void compress(Path inputFile, OutputStream outputStream) throws IOException {
        try (InputStream inputStream = Files.newInputStream(inputFile)) {
            this.compress(inputStream, outputStream);
        }
    }

    public void compress(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] inputData = this.readBytes(inputStream);
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
        this.decompress(inputFile, (Path)null);
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
        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(inputFile))) {
            this.decompress(inputStream, outputStream);
        }
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
}

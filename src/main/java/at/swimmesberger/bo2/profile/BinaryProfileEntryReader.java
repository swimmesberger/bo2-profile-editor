package at.swimmesberger.bo2.profile;


import at.swimmesberger.bo2.profile.util.CountingInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BinaryProfileEntryReader extends AbstractProfileEntryReader {
    private final DataInputStream in;
    private final CountingInputStream countingIn;

    /**
     * InputStream of an uncompressed bo2 profile file
     *
     * @param in
     */
    public BinaryProfileEntryReader(InputStream in) {
        Objects.requireNonNull(in);
        this.countingIn = new CountingInputStream(in);
        this.in = new DataInputStream(this.countingIn);
    }

    @Override
    public BinaryProfileEntryIterator iterateEntries() throws IOException {
        long entryCount = Integer.toUnsignedLong(this.in.readInt());
        return new BinaryProfileEntryIterator(this, entryCount);
    }

    private ProfileEntry<?> readNextEntry() throws IOException {
        // not sure what this byte does but when it's non zero it seems "correct"
        byte entryType = this.in.readByte();
        // the first entry seems to be of type = 1 all others have the type = 2
        if (entryType != 0) {
            ProfileEntry<?> entry = this.readNextEntryImpl();
            // separation byte? (always 0)
            this.in.skipBytes(1);
            return entry;
        } else {
            // broken entry
            this.in.skipBytes(6);
            return null;
        }
    }

    private ProfileEntry<?> readNextEntryImpl() throws IOException {
        int id = this.in.readInt();
        int dataTypeValue = this.in.readUnsignedByte();
        ProfileEntryDataType dataType = ProfileEntryDataType.byValue(dataTypeValue);
        if (dataType == null) {
            throw new IllegalStateException("Invalid data type '" + dataTypeValue + "'");
        }
        long offset = this.countingIn.getByteCount();
        ProfileEntry<?> entry;
        switch (dataType) {
            case Int32:
                long int32Value = Integer.toUnsignedLong(this.in.readInt());
                entry = new ProfileEntry<>(id, offset, 4, ProfileEntryDataType.Int32, int32Value);
                break;
            case String:
                int stringLength = (int) Integer.toUnsignedLong(this.in.readInt());
                byte[] asciiBytes = new byte[stringLength];
                int stringReadBytes = this.in.read(asciiBytes);
                if (stringReadBytes != stringLength) throw new EOFException("Unexpected end of file!");
                String stringValue = new String(asciiBytes, StandardCharsets.US_ASCII);
                entry = new ProfileEntry<>(id, offset, stringLength, ProfileEntryDataType.String, stringValue);
                break;
            case Float:
                float precisionValue = this.in.readFloat();
                entry = new ProfileEntry<>(id, offset, 4, ProfileEntryDataType.Float, precisionValue);
                break;
            case Binary:
                int binaryLength = (int) Integer.toUnsignedLong(this.in.readInt());
                byte[] binData = new byte[binaryLength];
                int binReadBytes = this.in.read(binData);
                if (binReadBytes != binaryLength) throw new EOFException("Unexpected end of file!");
                entry = new ProfileEntry<>(id, offset, binaryLength, ProfileEntryDataType.Binary, binData);
                break;
            case Int8:
                int int8Value = this.in.readUnsignedByte();
                entry = new ProfileEntry<>(id, offset, 1, ProfileEntryDataType.Int8, int8Value);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported data type " + dataType);
        }
        return entry;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    private static class BinaryProfileEntryIterator implements ProfileEntryIterator {
        private final BinaryProfileEntryReader reader;
        private final long entryCount;

        private long allReadEntries;
        private long brokenEntries;

        public BinaryProfileEntryIterator(BinaryProfileEntryReader reader, long entryCount) {
            this.reader = Objects.requireNonNull(reader);
            this.entryCount = entryCount;
            this.allReadEntries = 0;
            this.brokenEntries = 0;
        }

        @Override
        public boolean hasNext() {
            return this.allReadEntries < this.entryCount;
        }

        @Override
        public ProfileEntry<?> next() {
            try {
                ProfileEntry<?> entry = this.reader.readNextEntry();
                if (entry == null) {
                    this.brokenEntries++;
                }
                this.allReadEntries++;
                return entry;
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }

        public long getEntryCount() {
            return entryCount;
        }

        public long getAllReadEntries() {
            return allReadEntries;
        }

        public long getBrokenEntries() {
            return brokenEntries;
        }
    }
}

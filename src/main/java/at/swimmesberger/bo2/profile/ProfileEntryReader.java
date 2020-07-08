package at.swimmesberger.bo2.profile;

import org.apache.commons.io.input.CountingInputStream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileEntryReader implements AutoCloseable {
    private final DataInputStream in;
    private final CountingInputStream countingIn;

    public ProfileEntryReader(InputStream in) {
        Objects.requireNonNull(in);
        this.countingIn = new CountingInputStream(in);
        this.in = new DataInputStream(this.countingIn);
    }

    public List<ProfileEntry<?>> readEntries() throws IOException {
        long entryCount = Integer.toUnsignedLong(this.in.readInt());
        List<ProfileEntry<?>> entries = new ArrayList<>((int) entryCount);
        while (this.in.readByte() != 0) {
            ProfileEntry<?> entry = this.readNextEntry();
            if(entry != null) {
                entries.add(entry);
            }
        }
        return entries;
    }

    private ProfileEntry<?> readNextEntry() throws IOException {
        byte isValid = this.in.readByte();
        if (isValid != 0) {
            ProfileEntry<?> entry = this.readNextEntryImpl();
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
        long offset = this.countingIn.getCount();
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
}

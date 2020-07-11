package at.swimmesberger.bo2.profile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BinaryProfileEntryWriter implements ProfileEntryWriter {
    private final DataOutputStream out;
    private long entryCount;

    public BinaryProfileEntryWriter(OutputStream out) {
        Objects.requireNonNull(out);
        this.out = new DataOutputStream(out);
        this.entryCount = 0;
    }

    @Override
    public void begin(long entryCount) throws IOException {
        this.out.writeInt((int) entryCount);
    }

    @Override
    public void write(ProfileEntry<?> entry) throws IOException {
        ProfileEntryDataType dataType = entry.getDataType();
        // entry validation marker?
        if (this.entryCount == 0) {
            // the first entry seems to have 1 for this value
            this.out.writeByte(1);
        } else {
            // the others have 2
            this.out.writeByte(2);
        }
        this.out.writeInt(entry.getId());
        this.out.writeByte(dataType.getValue());
        switch (dataType) {
            case Int32:
                long intValue = (Long) entry.getValue();
                this.out.writeInt((int) intValue);
                break;
            case String:
                String stringValue = (String) entry.getValue();
                byte[] asciiBytes = stringValue.getBytes(StandardCharsets.US_ASCII);
                this.out.writeInt(stringValue.length());
                this.out.write(asciiBytes);
                break;
            case Float:
                float precisionValue = (Float) entry.getValue();
                this.out.writeFloat(precisionValue);
                break;
            case Binary:
                byte[] binData = (byte[]) entry.getValue();
                this.out.writeInt(binData.length);
                this.out.write(binData);
                break;
            case Int8:
                int int8Value = (Integer) entry.getValue();
                this.out.writeByte(int8Value);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported data type " + dataType);
        }
        // separation byte?
        this.out.writeByte(0);
        this.entryCount += 1;
    }

    @Override
    public void end() throws IOException {

    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}

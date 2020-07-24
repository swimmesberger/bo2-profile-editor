package at.swimmesberger.bo2.profile;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class ProfileEntry<T> {
    private final byte type;
    private final int id;
    private final long offset;
    private final long length;
    private final ProfileEntryDataType dataType;
    private final T value;

    public ProfileEntry(int id, ProfileEntryDataType dataType, T value) {
        this(2, id, -1, -1, dataType, value);
    }

    public ProfileEntry(int type, int id, long offset, long length, ProfileEntryDataType dataType, T value) {
        this((byte) type, id, offset, length, dataType, value);
    }

    public ProfileEntry(byte type, int id, long offset, long length, ProfileEntryDataType dataType, T value) {
        this.type = type;
        this.id = id;
        this.offset = offset;
        this.length = length;
        this.dataType = Objects.requireNonNull(dataType);
        this.value = Objects.requireNonNull(value);
        if (!dataType.getJavaType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("The expected java type of the ProfileEntryDataType '" +
                    dataType.getJavaType().getSimpleName() +
                    "' does not match the passed value type '" +
                    value.getClass().getSimpleName() + "'");
        }
    }

    private static int valueHash(Object value) {
        if (value instanceof byte[]) {
            byte[] b1 = (byte[]) value;
            return Arrays.hashCode(b1);
        } else {
            return Objects.hashCode(value);
        }
    }

    private static String valueString(Object value) {
        if (value instanceof byte[]) {
            byte[] b1 = (byte[]) value;
            return Base64.getEncoder().encodeToString(b1);
        } else {
            return Objects.toString(value);
        }
    }

    private static boolean valueEquals(Object o1Value, Object o2Value) {
        if (o1Value instanceof byte[] && o2Value instanceof byte[]) {
            byte[] b1 = (byte[]) o1Value;
            byte[] b2 = (byte[]) o2Value;
            return Arrays.equals(b1, b2);
        } else {
            return Objects.equals(o1Value, o2Value);
        }
    }

    public byte getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public long getOffset() {
        return offset;
    }

    public long getLength() {
        return length;
    }

    public ProfileEntryDataType getDataType() {
        return dataType;
    }

    public T getValue() {
        return value;
    }

    public String getValueString() {
        return valueString(this.getValue());
    }

    public String[] toStringArray() {
        return new String[]{String.valueOf(this.getType()), String.valueOf(this.getId()), String.valueOf(this.getOffset()), String.valueOf(this.getLength()), String.valueOf(this.getDataType()), valueString(this.getValue())};
    }

    public ProfileEntry<T> withValue(T value) {
        return new ProfileEntry<>(this.getType(), this.getId(), this.getOffset(), this.getLength(), this.getDataType(), value);
    }

    @Override
    public String toString() {
        return "ProfileEntry{" +
                "indicator=" + type +
                ", id=" + id +
                ", offset=" + offset +
                ", length=" + length +
                ", dataType=" + dataType +
                ", value=" + valueString(value) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileEntry<?> that = (ProfileEntry<?>) o;
        return type == that.type &&
                id == that.id &&
                offset == that.offset &&
                length == that.length &&
                dataType == that.dataType &&
                valueEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, offset, length, dataType, valueHash(value));
    }
}

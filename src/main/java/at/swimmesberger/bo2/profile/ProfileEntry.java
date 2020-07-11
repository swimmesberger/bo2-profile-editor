package at.swimmesberger.bo2.profile;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class ProfileEntry<T> {
    private final int id;
    private final long offset;
    private final long length;
    private final ProfileEntryDataType dataType;
    private final T value;

    public ProfileEntry(int id, long offset, long length, ProfileEntryDataType dataType, T value) {
        this.id = id;
        this.offset = offset;
        this.length = length;
        this.dataType = Objects.requireNonNull(dataType);
        this.value = Objects.requireNonNull(value);
    }

    public static <T> ProfileEntryBuilder<T> builder() {
        return new ProfileEntryBuilder<T>();
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
        return new String[]{String.valueOf(this.getId()), String.valueOf(this.getOffset()), String.valueOf(this.getLength()), String.valueOf(this.getDataType()), valueString(this.getValue())};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileEntry<?> that = (ProfileEntry<?>) o;
        return id == that.id &&
                offset == that.offset &&
                length == that.length &&
                dataType == that.dataType &&
                valueEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, offset, length, dataType, valueHash(value));
    }

    @Override
    public String toString() {
        return "ProfileEntry{" +
                "id=" + id +
                ", offset=" + offset +
                ", length=" + length +
                ", dataType=" + dataType +
                ", value=" + valueString(value) +
                '}';
    }

    private boolean valueEquals(T o1Value, Object o2Value) {
        if (o1Value instanceof byte[] && o2Value instanceof byte[]) {
            byte[] b1 = (byte[]) o1Value;
            byte[] b2 = (byte[]) o2Value;
            return Arrays.equals(b1, b2);
        } else {
            return Objects.equals(o1Value, o2Value);
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

    public static final class ProfileEntryBuilder<T> {
        private int id;
        private long offset;
        private long length;
        private ProfileEntryDataType dataType;
        private T value;

        private ProfileEntryBuilder() {
            this(null);
        }

        private ProfileEntryBuilder(ProfileEntry<T> entry) {
            if (entry != null) {
                this.id = entry.getId();
                this.offset = entry.getOffset();
                this.length = entry.getLength();
                this.dataType = entry.getDataType();
                this.value = entry.getValue();
            }
        }

        public ProfileEntryBuilder<T> withId(int id) {
            this.id = id;
            return this;
        }

        public ProfileEntryBuilder<T> withOffset(long offset) {
            this.offset = offset;
            return this;
        }

        public ProfileEntryBuilder<T> withLength(long length) {
            this.length = length;
            return this;
        }

        public ProfileEntryBuilder<T> withDataType(ProfileEntryDataType dataType) {
            this.dataType = dataType;
            return this;
        }


        public ProfileEntryBuilder<T> withValue(T value) {
            this.value = value;
            return this;
        }

        public ProfileEntry<T> build() {
            return new ProfileEntry<>(id, offset, length, dataType, value);
        }
    }
}

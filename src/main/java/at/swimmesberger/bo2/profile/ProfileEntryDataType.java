package at.swimmesberger.bo2.profile;

public enum ProfileEntryDataType {
    Int32(1, long.class), String(4, String.class), Float(5, float.class), Binary(6, byte[].class), Int8(8, int.class);

    private final int value;
    private final Class<?> dataType;

    private ProfileEntryDataType(int value, Class<?> dataType) {
        this.value = value;
        this.dataType = dataType;
    }

    public static ProfileEntryDataType byValue(int value) {
        for (ProfileEntryDataType type : ProfileEntryDataType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}

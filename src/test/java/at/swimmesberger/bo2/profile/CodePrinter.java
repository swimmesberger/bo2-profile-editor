package at.swimmesberger.bo2.profile;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CodePrinter {
    public String printProfileEntries(Collection<ProfileEntry<?>> entries) {
        StringBuilder sb = new StringBuilder();
        this.printProfileEntries(entries, sb);
        return sb.toString();
    }

    public void printProfileEntries(Collection<ProfileEntry<?>> entries, StringBuilder output) {
        output.append("Arrays.asList(");
        String entriesString = entries.stream().map(this::printProfileEntryConstructor).collect(Collectors.joining(", \r\n"));
        output.append(entriesString);
        output.append("\n);");
    }

    public String printProfileEntryConstructor(ProfileEntry<?> entry) {
        StringBuilder sb = new StringBuilder();
        this.printProfileEntryConstructor(entry, sb);
        return sb.toString();
    }

    public void printProfileEntryConstructor(ProfileEntry<?> entry, StringBuilder output) {
        output.append("new ProfileEntry").append("<").append(">(");
        output.append(entry.getId()).append(", ");
        output.append(entry.getOffset()).append("L, ");
        output.append(entry.getLength()).append("L, ");
        output.append("ProfileEntryDataType.").append(entry.getDataType().name()).append(", ");
        this.printValue(entry.getDataType(), entry.getValue(), output);
        output.append(")");
    }

    private void printValue(ProfileEntryDataType type, Object value, StringBuilder output) {
        switch (type) {
            case Int32:
                output.append(value).append("L");
                break;
            case String:
                output.append("\"").append(value).append("\"");
                break;
            case Float:
                output.append(value).append("f");
                break;
            case Binary:
                byte[] bArr = (byte[]) value;
                output.append("new byte[]{");
                output.append(intStream(bArr).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
                output.append("}");
                break;
            case Int8:
                output.append(value);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static IntStream intStream(byte[] array) {
        return IntStream.range(0, array.length).map(idx -> array[idx]);
    }
}

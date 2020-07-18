package at.swimmesberger.bo2.profile.conversion;


import at.swimmesberger.bo2.profile.ProfileEntry;
import at.swimmesberger.bo2.profile.ProfileEntryWriter;
import at.swimmesberger.bo2.profile.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TableProfileEntryWriter implements ProfileEntryWriter {
    private static final String[] COLUMN_NAMES = new String[]{"ID", "OFF", "LEN", "DATA_TYPE", "VALUE"};
    private static final int[] COLUMN_WIDTH = new int[]{5, 5, 5, 10, 10};

    private final PrintStream outWriter;
    private final String[] formats;
    private final int fullWidth;

    public TableProfileEntryWriter(OutputStream out) {
        try {
            this.outWriter = new PrintStream(out, false, StandardCharsets.UTF_8.name());
            this.formats = resolveFormats(COLUMN_WIDTH);
            this.fullWidth = Arrays.stream(COLUMN_WIDTH).sum() + (COLUMN_WIDTH.length * 2);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void begin(long entryCount) throws IOException {
        this.outWriter.append(StringUtils.repeat("_", this.fullWidth));
        this.outWriter.println();

        for (int i = 0; i<this.formats.length; i++) {
            String columnName = COLUMN_NAMES[i];
            this.outWriter.format(this.formats[i], columnName);
        }

        this.outWriter.append("|");
        this.outWriter.append(StringUtils.repeat("=", this.fullWidth-1));
        this.outWriter.append("|");
        this.outWriter.println();

        if(this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
    }

    @Override
    public void write(ProfileEntry<?> entry) throws IOException {
        String[] dataArray = entry.toStringArray();
        for (int i = 0; i<this.formats.length; i++) {
            int columnWidth = COLUMN_WIDTH[i];
            String valueString = dataArray[i];
            if(valueString.length() > columnWidth) {
                valueString = valueString.substring(0, columnWidth-2);
                valueString = valueString + "..";
            }
            this.outWriter.format(this.formats[i], valueString);
        }

        if(this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
    }

    @Override
    public void end() throws IOException {
        this.outWriter.append(StringUtils.repeat("-", this.fullWidth + 1));
        this.outWriter.println();
    }

    @Override
    public void close() throws IOException {
        if(this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
        this.outWriter.close();
    }

    private static String[] resolveFormats(int[] columnWidth) {
        String[] formats = new String[columnWidth.length];
        for (int i = 0; i < columnWidth.length; i++) {
            StringBuilder sb = new StringBuilder();
            if (i == 0) {
                sb.append("|");
            }
            sb.append(" %1$-");
            sb.append(columnWidth[i]);
            sb.append("s|");
            sb.append(i + 1 == columnWidth.length ? "\n" : "");
            formats[i] = sb.toString();
        }
        return formats;
    }
}

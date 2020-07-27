package at.swimmesberger.bo2.profile.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TablePrinter implements AutoCloseable {
    private static final String HEADER_START_CHAR = "_";
    private static final String HEADER_END_CHAR = "=";
    private static final String TRAILING_CHAR = "-";
    private static final String COLUMN_SEPARATOR = "|";

    private final PrintStream outWriter;

    private volatile String[] columnNames;
    private volatile int[] columnWidths;
    private volatile String[] formats;
    private volatile int fullWidth;

    public TablePrinter(OutputStream out) {
        this(out, null, null);
    }

    public TablePrinter(OutputStream out, String[] columnNames, int[] columnWidths) {
        try {
            this.outWriter = new PrintStream(out, false, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        this.setColumnNames(columnNames);
        this.setColumnWidth(columnWidths);
    }

    private static String[] resolveFormats(int[] columnWidth) {
        String[] formats = new String[columnWidth.length];
        for (int i = 0; i < columnWidth.length; i++) {
            StringBuilder sb = new StringBuilder();
            if (i == 0) {
                sb.append(COLUMN_SEPARATOR);
            }
            sb.append(" %1$-");
            sb.append(columnWidth[i]);
            sb.append("s" + COLUMN_SEPARATOR);
            sb.append(i + 1 == columnWidth.length ? "\n" : "");
            formats[i] = sb.toString();
        }
        return formats;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void setColumnWidth(int[] columnWidths) {
        this.initWidths(columnWidths);
    }

    public void writeHeader() throws IOException {
        this.outWriter.append(StringUtils.repeat(HEADER_START_CHAR, this.fullWidth));
        this.outWriter.println();

        for (int i = 0; i < this.formats.length; i++) {
            String columnName = this.columnNames[i];
            this.outWriter.format(this.formats[i], columnName);
        }

        this.outWriter.append(COLUMN_SEPARATOR);
        this.outWriter.append(StringUtils.repeat(HEADER_END_CHAR, this.fullWidth - 1));
        this.outWriter.append(COLUMN_SEPARATOR);
        this.outWriter.println();
        if(this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
    }

    public void writeRow(String[] dataRow) throws IOException {
        for (int i = 0; i < this.formats.length; i++) {
            int columnWidth = this.columnWidths[i];
            String valueString = dataRow[i];
            if (valueString.length() > columnWidth) {
                valueString = valueString.substring(0, columnWidth - 2);
                valueString = valueString + "..";
            }
            this.outWriter.format(this.formats[i], valueString);
        }
        if(this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
    }

    public void writeTrailing() throws IOException {
        this.outWriter.append(StringUtils.repeat(TRAILING_CHAR, this.fullWidth + 1));
        this.outWriter.println();
        if(this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
    }

    @Override
    public void close() throws IOException {
        if (this.outWriter.checkError()) {
            throw new IOException("Failed to write table data to stream");
        }
        this.outWriter.close();
    }

    private void initWidths(int[] columnWidths) {
        this.columnWidths = columnWidths;
        if (columnWidths == null) {
            this.formats = null;
            this.fullWidth = 0;
        } else {
            this.formats = resolveFormats(columnWidths);
            this.fullWidth = Arrays.stream(columnWidths).sum() + (columnWidths.length * 2);
        }
    }
}

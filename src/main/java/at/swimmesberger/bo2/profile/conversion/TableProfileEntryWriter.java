package at.swimmesberger.bo2.profile.conversion;


import at.swimmesberger.bo2.profile.ProfileEntry;
import at.swimmesberger.bo2.profile.ProfileEntryWriter;
import at.swimmesberger.bo2.profile.util.TablePrinter;

import java.io.IOException;
import java.io.OutputStream;

public class TableProfileEntryWriter implements ProfileEntryWriter {
    private static final String[] COLUMN_NAMES = new String[]{"TYPE", "ID", "OFF", "LEN", "DATA_TYPE", "VALUE"};
    private static final int[] COLUMN_WIDTH = new int[]{5, 5, 5, 5, 10, 10};

    private final TablePrinter tablePrinter;

    public TableProfileEntryWriter(OutputStream out) {
        this.tablePrinter = new TablePrinter(out, COLUMN_NAMES, COLUMN_WIDTH);
    }

    @Override
    public void begin(long entryCount) throws IOException {
        this.tablePrinter.writeHeader();
    }

    @Override
    public void write(ProfileEntry<?> entry) throws IOException {
        String[] dataArray = entry.toStringArray();
        this.tablePrinter.writeRow(dataArray);
    }

    @Override
    public void end() throws IOException {
        this.tablePrinter.writeTrailing();
    }

    @Override
    public void close() throws IOException {
        this.tablePrinter.close();
    }
}

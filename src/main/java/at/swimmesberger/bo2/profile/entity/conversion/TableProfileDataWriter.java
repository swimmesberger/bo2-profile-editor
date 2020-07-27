package at.swimmesberger.bo2.profile.entity.conversion;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import at.swimmesberger.bo2.profile.util.TablePrinter;

import java.io.IOException;
import java.io.OutputStream;

public class TableProfileDataWriter implements ProfileDataWriter {
    private static final String[] COLUMN_NAMES = new String[]{"NAME", "VALUE"};
    private static final int[] COLUMN_WIDTH = new int[]{25, 10};

    private final TablePrinter tablePrinter;

    public TableProfileDataWriter(OutputStream outputStream) {
        this.tablePrinter = new TablePrinter(outputStream, COLUMN_NAMES, COLUMN_WIDTH);
    }

    @Override
    public void writeData(ProfileData profileData) throws IOException {
        this.tablePrinter.writeHeader();
        for (ProfileDataValueType type : ProfileDataValueType.values()) {
            this.tablePrinter.writeRow(new String[]{type.name(), String.valueOf(profileData.getValue(type))});
        }
        this.tablePrinter.writeTrailing();
    }

    @Override
    public void close() throws IOException {
        this.tablePrinter.close();
    }
}

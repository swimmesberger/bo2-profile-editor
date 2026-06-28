package at.swimmesberger.bo2.profile.stash;

import java.util.Arrays;

public class StashSlot {
    private final int index;
    private final int entryId;
    private final byte[] data;

    public StashSlot(int index, int entryId, byte[] data) {
        this.index = index;
        this.entryId = entryId;
        this.data = Arrays.copyOf(data, data.length);
    }

    public int getIndex() {
        return index;
    }

    public int getEntryId() {
        return entryId;
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public boolean isEmpty() {
        if (data.length == 0) return true;
        // BL2 stores empty stash slots as 40 zero bytes rather than a 0-length entry.
        // Treat any all-zero byte array as empty so list/add/clear work on real profiles.
        for (byte b : data) {
            if (b != 0) return false;
        }
        return true;
    }
}

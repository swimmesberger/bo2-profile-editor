package at.swimmesberger.bo2.profile.stash;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntry;
import at.swimmesberger.bo2.profile.ProfileEntryDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maps Claptrap's Stash bank slots to/from ProfileEntries.
 *
 * In profile.bin the stash is stored as consecutive Binary entries starting
 * at entry ID 130. Each slot is a Binary entry: 0 bytes = empty, non-zero
 * bytes = serialized item data that Gibbed codes wrap.
 */
public class StashConverter {
    static final int SLOT_ID_START = 130;
    static final int SLOT_ID_END   = 159; // up to 30 slots; max SDU-expanded stash is 27

    // BL2 uses 40 zero bytes as the canonical empty-slot placeholder rather than a 0-length entry.
    public static final byte[] EMPTY_SLOT_DATA = new byte[40];

    public List<StashSlot> readStash(ProfileEntries entries) {
        List<StashSlot> slots = new ArrayList<>();
        for (int id = SLOT_ID_START; id <= SLOT_ID_END; id++) {
            ProfileEntry<?> entry = entries.getEntry(id);
            if (entry != null && entry.getDataType() == ProfileEntryDataType.Binary) {
                byte[] data = (byte[]) entry.getValue();
                slots.add(new StashSlot(slots.size(), id, data));
            }
        }
        return Collections.unmodifiableList(slots);
    }

    public ProfileEntries writeSlot(ProfileEntries entries, int slotIndex, byte[] data) {
        List<StashSlot> slots = readStash(entries);
        if (slotIndex < 0 || slotIndex >= slots.size()) {
            throw new IllegalArgumentException(
                    "Slot " + (slotIndex + 1) + " does not exist (stash has " + slots.size() + " slot(s)).");
        }
        int entryId = slots.get(slotIndex).getEntryId();
        ProfileEntry<?> existing = entries.getEntry(entryId);
        ProfileEntry<byte[]> updated = existing != null
                ? ((ProfileEntry<byte[]>) existing).withValue(data)
                : new ProfileEntry<>(entryId, ProfileEntryDataType.Binary, data);
        return ProfileEntries.builder(entries).withEntry(updated).build();
    }

    public int findFirstEmptySlot(List<StashSlot> slots) {
        for (StashSlot slot : slots) {
            if (slot.isEmpty()) {
                return slot.getIndex();
            }
        }
        return -1;
    }
}

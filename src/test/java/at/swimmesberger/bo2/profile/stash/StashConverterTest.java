package at.swimmesberger.bo2.profile.stash;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntry;
import at.swimmesberger.bo2.profile.ProfileEntryDataType;
import at.swimmesberger.bo2.profile.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StashConverterTest {

    private static final byte[] ITEM_BYTES = {1, 2, 3, 4, 5};

    private ProfileEntries profile1() {
        return ProfileEntries.from(TestFixtures.createProfile1Entries());
    }

    private ProfileEntries withItemInSlot(int slotEntryId) {
        List<ProfileEntry<?>> list = new java.util.ArrayList<>(TestFixtures.createProfile1Entries());
        list.removeIf(e -> e.getId() == slotEntryId);
        list.add(new ProfileEntry<>(2, slotEntryId, -1, ITEM_BYTES.length,
                ProfileEntryDataType.Binary, ITEM_BYTES.clone()));
        return ProfileEntries.from(list);
    }

    private ProfileEntries noStashSlots() {
        return ProfileEntries.from(Arrays.asList(
                new ProfileEntry<>(2, 136, -1, 4, ProfileEntryDataType.Int32, 1315L)
        ));
    }

    // ── readStash ─────────────────────────────────────────────────────────────

    @Test
    public void readStash_profile1_returnsFourSlots() {
        List<StashSlot> slots = new StashConverter().readStash(profile1());
        assertEquals(4, slots.size());
    }

    @Test
    public void readStash_profile1_allSlotsEmpty() {
        List<StashSlot> slots = new StashConverter().readStash(profile1());
        for (StashSlot slot : slots) {
            assertTrue(slot.isEmpty(), "Expected slot " + slot.getIndex() + " to be empty");
        }
    }

    @Test
    public void readStash_profile1_indicesAreZeroBased() {
        List<StashSlot> slots = new StashConverter().readStash(profile1());
        for (int i = 0; i < slots.size(); i++) {
            assertEquals(i, slots.get(i).getIndex());
        }
    }

    @Test
    public void readStash_profile1_entryIdsStartAt130() {
        List<StashSlot> slots = new StashConverter().readStash(profile1());
        assertEquals(130, slots.get(0).getEntryId());
        assertEquals(131, slots.get(1).getEntryId());
        assertEquals(132, slots.get(2).getEntryId());
        assertEquals(133, slots.get(3).getEntryId());
    }

    @Test
    public void readStash_noStashEntries_returnsEmptyList() {
        List<StashSlot> slots = new StashConverter().readStash(noStashSlots());
        assertTrue(slots.isEmpty());
    }

    @Test
    public void readStash_slotWithItem_isNotEmpty() {
        ProfileEntries entries = withItemInSlot(130);
        List<StashSlot> slots = new StashConverter().readStash(entries);
        assertFalse(slots.get(0).isEmpty());
    }

    @Test
    public void readStash_slotWithItem_returnsCorrectData() {
        ProfileEntries entries = withItemInSlot(130);
        List<StashSlot> slots = new StashConverter().readStash(entries);
        assertArrayEquals(ITEM_BYTES, slots.get(0).getData());
    }

    @Test
    public void readStash_onlyOccupiedSlotIsNonEmpty() {
        ProfileEntries entries = withItemInSlot(131); // slot index 1
        List<StashSlot> slots = new StashConverter().readStash(entries);
        assertTrue(slots.get(0).isEmpty());
        assertFalse(slots.get(1).isEmpty());
        assertTrue(slots.get(2).isEmpty());
        assertTrue(slots.get(3).isEmpty());
    }

    // ── writeSlot ─────────────────────────────────────────────────────────────

    @Test
    public void writeSlot_firstSlot_updatesEntryId130() {
        ProfileEntries modified = new StashConverter().writeSlot(profile1(), 0, ITEM_BYTES);
        assertArrayEquals(ITEM_BYTES, (byte[]) modified.getEntry(130).getValue());
    }

    @Test
    public void writeSlot_secondSlot_updatesEntryId131() {
        ProfileEntries modified = new StashConverter().writeSlot(profile1(), 1, ITEM_BYTES);
        assertArrayEquals(ITEM_BYTES, (byte[]) modified.getEntry(131).getValue());
    }

    @Test
    public void writeSlot_doesNotAffectOtherSlots() {
        ProfileEntries modified = new StashConverter().writeSlot(profile1(), 0, ITEM_BYTES);
        List<StashSlot> slots = new StashConverter().readStash(modified);
        assertTrue(slots.get(1).isEmpty());
        assertTrue(slots.get(2).isEmpty());
        assertTrue(slots.get(3).isEmpty());
    }

    @Test
    public void writeSlot_emptyBytes_clearsSlot() {
        ProfileEntries withItem = new StashConverter().writeSlot(profile1(), 0, ITEM_BYTES);
        ProfileEntries cleared = new StashConverter().writeSlot(withItem, 0, new byte[0]);
        assertTrue(new StashConverter().readStash(cleared).get(0).isEmpty());
    }

    @Test
    public void writeSlot_emptySlotData_clearsSlot() {
        ProfileEntries withItem = new StashConverter().writeSlot(profile1(), 0, ITEM_BYTES);
        ProfileEntries cleared = new StashConverter().writeSlot(withItem, 0, StashConverter.EMPTY_SLOT_DATA);
        assertTrue(new StashConverter().readStash(cleared).get(0).isEmpty());
    }

    @Test
    public void writeSlot_preservesUnrelatedEntries() {
        ProfileEntries modified = new StashConverter().writeSlot(profile1(), 0, ITEM_BYTES);
        // Badass rank entry (ID 136) must be unchanged
        assertEquals(1315L, modified.getEntry(136).getValue());
    }

    @Test
    public void writeSlot_negativeIndex_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new StashConverter().writeSlot(profile1(), -1, ITEM_BYTES));
    }

    @Test
    public void writeSlot_indexEqualToSlotCount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new StashConverter().writeSlot(profile1(), 4, ITEM_BYTES)); // 4 slots → valid range 0-3
    }

    @Test
    public void writeSlot_indexFarOutOfRange_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new StashConverter().writeSlot(profile1(), 100, ITEM_BYTES));
    }

    @Test
    public void readStash_allZeroSlot_isRecognizedAsEmpty() {
        // Real BL2 profiles use 40-zero-byte arrays for empty slots, not 0-byte entries.
        List<ProfileEntry<?>> list = new java.util.ArrayList<>(TestFixtures.createProfile1Entries());
        list.removeIf(e -> e.getId() == 130);
        list.add(new ProfileEntry<>(2, 130, -1, StashConverter.EMPTY_SLOT_DATA.length,
                ProfileEntryDataType.Binary, StashConverter.EMPTY_SLOT_DATA.clone()));
        ProfileEntries entries = ProfileEntries.from(list);
        List<StashSlot> slots = new StashConverter().readStash(entries);
        assertTrue(slots.get(0).isEmpty(), "40-zero-byte slot should be treated as empty");
    }

    // ── findFirstEmptySlot ────────────────────────────────────────────────────

    @Test
    public void findFirstEmptySlot_allEmpty_returnsZero() {
        List<StashSlot> slots = new StashConverter().readStash(profile1());
        assertEquals(0, new StashConverter().findFirstEmptySlot(slots));
    }

    @Test
    public void findFirstEmptySlot_firstOccupied_returnsOne() {
        ProfileEntries entries = withItemInSlot(130); // index 0 occupied
        List<StashSlot> slots = new StashConverter().readStash(entries);
        assertEquals(1, new StashConverter().findFirstEmptySlot(slots));
    }

    @Test
    public void findFirstEmptySlot_allFull_returnsNegativeOne() {
        ProfileEntries entries = profile1();
        StashConverter converter = new StashConverter();
        // Fill every slot
        for (int i = 0; i < 4; i++) {
            entries = converter.writeSlot(entries, i, ITEM_BYTES);
        }
        List<StashSlot> slots = converter.readStash(entries);
        assertEquals(-1, converter.findFirstEmptySlot(slots));
    }

    @Test
    public void findFirstEmptySlot_emptyList_returnsNegativeOne() {
        assertEquals(-1, new StashConverter().findFirstEmptySlot(java.util.Collections.emptyList()));
    }
}

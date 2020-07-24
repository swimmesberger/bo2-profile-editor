package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntry;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ProfileDataConverter {
    private static final int BADASS_RANK_ID = 136;
    private static final int BADASS_TOKENS_ID = 138;
    private static final int STATS_ID = 143;
    private static final int GOLDEN_KEYS_ID = 162;
    private static final int CUSTOMIZATIONS_ID = 300;

    private final ProfileStatsEncoding statsDecoder;

    public ProfileDataConverter() {
        this.statsDecoder = new ProfileStatsEncoding();
    }

    public ProfileData convert(ProfileEntries entries) {
        int goldenKeys = this.getGoldenKeys(entries);
        ProfileStats profileStats = this.getProfileStats(entries);
        long badassRank = this.getBadassRank(entries);
        long badassTokens = this.getBadassTokens(entries);
        ProfileCustomizations customizations = getCustomizations(entries);
        return new ProfileData(goldenKeys, profileStats, badassRank, badassTokens, customizations);
    }

    // 300 = customizations
    // 255 = UNLOCKED, 0 = LOCKED
    private ProfileCustomizations getCustomizations(ProfileEntries entries) {
        ProfileEntry<byte[]> statsEntry = (ProfileEntry<byte[]>) entries.getEntry(CUSTOMIZATIONS_ID);
        byte[] data = statsEntry.getValue();
        int[] customData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            int value = Byte.toUnsignedInt(data[i]);
            customData[i] = value;
        }
        return new ProfileCustomizations(customData);
    }

    private long getBadassRank(ProfileEntries entries) {
        ProfileEntry<Long> statsEntry = (ProfileEntry<Long>) entries.getEntry(BADASS_RANK_ID);
        return statsEntry.getValue();
    }

    private long getBadassTokens(ProfileEntries entries) {
        ProfileEntry<Long> statsEntry = (ProfileEntry<Long>) entries.getEntry(BADASS_TOKENS_ID);
        return statsEntry.getValue();
    }

    private int getGoldenKeys(ProfileEntries entries) {
        // golden key data structure
        // 1 byte = unknown
        // 1 byte - 1 byte = value
        // 1 byte = unknown
        // 1 byte = 1 byte = value
        // ...
        ProfileEntry<byte[]> goldenKeyEntry = (ProfileEntry<byte[]>) entries.getEntry(GOLDEN_KEYS_ID);
        byte[] goldenKeyData = goldenKeyEntry.getValue();
        int goldenKeys = 0;
        try (ByteArrayInputStream bIn = new ByteArrayInputStream(goldenKeyData); DataInputStream dIn = new DataInputStream(bIn)) {
            int indicatorByte;
            while ((indicatorByte = dIn.read()) != -1) {
                int subKeyVal1 = dIn.readByte();
                int subKeyVal2 = dIn.readByte();
                int subKeyVal = subKeyVal1 - subKeyVal2;
                goldenKeys += subKeyVal;
            }
        } catch (IOException ex) {
            //ignore
        }
        return goldenKeys;
    }

    // special string encoding
    private ProfileStats getProfileStats(ProfileEntries entries) {
        ProfileEntry<String> statsEntry = (ProfileEntry<String>) entries.getEntry(STATS_ID);
        String encodedString = statsEntry.getValue();
        return this.statsDecoder.decode(encodedString);
    }
}

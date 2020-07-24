package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntry;
import at.swimmesberger.bo2.profile.ProfileEntryDataType;

import java.io.*;

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

    public ProfileData decodeEntries(ProfileEntries entries) {
        int goldenKeys = this.getGoldenKeys(entries);
        ProfileStats profileStats = this.getProfileStats(entries);
        long badassRank = this.getBadassRank(entries);
        long badassTokens = this.getBadassTokens(entries);
        ProfileCustomizations customizations = getCustomizations(entries);
        return new ProfileData(goldenKeys, profileStats, badassRank, badassTokens, customizations);
    }

    public ProfileEntries encodeEntries(ProfileData data, ProfileEntries entries) {
        ProfileEntries.ProfileEntriesBuilder entriesBuilder = ProfileEntries.builder(entries);
        this.setGoldenKeys(entriesBuilder, data.getGoldenKeys());
        this.setProfileStats(entriesBuilder, data.getStats());
        this.setBadassRank(entriesBuilder, data.getBadassRank());
        this.setBadassToken(entriesBuilder, data.getBadassTokens());
        this.setProfileCustomizations(entriesBuilder, data.getCustomizations());
        return entriesBuilder.build();
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
        ProfileEntry<Long> badassEntry = (ProfileEntry<Long>) entries.getEntry(BADASS_RANK_ID);
        return badassEntry.getValue();
    }

    private long getBadassTokens(ProfileEntries entries) {
        ProfileEntry<Long> badassEntry = (ProfileEntry<Long>) entries.getEntry(BADASS_TOKENS_ID);
        return badassEntry.getValue();
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
            throw new UncheckedIOException(ex);
        }
        return goldenKeys;
    }

    // special string encoding
    private ProfileStats getProfileStats(ProfileEntries entries) {
        ProfileEntry<String> statsEntry = (ProfileEntry<String>) entries.getEntry(STATS_ID);
        String encodedString = statsEntry.getValue();
        return this.statsDecoder.decode(encodedString);
    }

    private void setGoldenKeys(ProfileEntries.ProfileEntriesBuilder builder, int goldenKeys) {
        byte[] goldenKeyData;
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream(); DataOutputStream dOut = new DataOutputStream(bOut)) {
            int writeCount = goldenKeys / 255;
            goldenKeyData = bOut.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        ProfileEntry<byte[]> goldenKeyEntry = (ProfileEntry<byte[]>) builder.getEntry(GOLDEN_KEYS_ID);
        if (goldenKeyEntry == null) {
            goldenKeyEntry = new ProfileEntry<>(GOLDEN_KEYS_ID, ProfileEntryDataType.Binary, goldenKeyData);
        } else {
            goldenKeyEntry = goldenKeyEntry.withValue(goldenKeyData);
        }
        builder.withEntry(goldenKeyEntry);
    }

    private void setBadassRank(ProfileEntries.ProfileEntriesBuilder builder, long badassRank) {
        ProfileEntry<Long> badassEntry = (ProfileEntry<Long>) builder.getEntry(BADASS_RANK_ID);
        if (badassEntry == null) {
            badassEntry = new ProfileEntry<>(BADASS_RANK_ID, ProfileEntryDataType.Int32, badassRank);
        } else {
            badassEntry = badassEntry.withValue(badassRank);
        }
        builder.withEntry(badassEntry);
    }

    private void setBadassToken(ProfileEntries.ProfileEntriesBuilder builder, long badassToken) {
        ProfileEntry<Long> badassEntry = (ProfileEntry<Long>) builder.getEntry(BADASS_TOKENS_ID);
        if (badassEntry == null) {
            badassEntry = new ProfileEntry<>(BADASS_TOKENS_ID, ProfileEntryDataType.Int32, badassToken);
        } else {
            badassEntry = badassEntry.withValue(badassToken);
        }
        builder.withEntry(badassEntry);
    }

    private void setProfileStats(ProfileEntries.ProfileEntriesBuilder builder, ProfileStats stats) {
        ProfileEntry<String> statsEntry = (ProfileEntry<String>) builder.getEntry(STATS_ID);
        String encodedStats = this.statsDecoder.encode(stats);
        if (statsEntry == null) {
            statsEntry = new ProfileEntry<>(STATS_ID, ProfileEntryDataType.String, encodedStats);
        } else {
            statsEntry = statsEntry.withValue(encodedStats);
        }
        builder.withEntry(statsEntry);
    }

    private void setProfileCustomizations(ProfileEntries.ProfileEntriesBuilder builder, ProfileCustomizations customizations) {
        ProfileEntry<byte[]> statsEntry = (ProfileEntry<byte[]>) builder.getEntry(CUSTOMIZATIONS_ID);
        int[] customData = customizations.getCustomizations();
        byte[] data = new byte[customData.length];
        for (int i = 0; i < data.length; i++) {
            int value = customData[i];
            data[i] = (byte) value;
        }
        if (statsEntry == null) {
            statsEntry = new ProfileEntry<>(CUSTOMIZATIONS_ID, ProfileEntryDataType.Binary, data);
        } else {
            statsEntry = statsEntry.withValue(data);
        }
        builder.withEntry(statsEntry);
    }
}

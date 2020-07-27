package at.swimmesberger.bo2.profile;

import at.swimmesberger.bo2.profile.entity.ProfileCustomizations;
import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileStats;

import java.util.Arrays;
import java.util.List;

public class TestFixtures {
    public static ProfileData createProfile1Data() {
        return new ProfileData(52, createMixedStats(), 1315, 10, ProfileCustomizations.allLocked());
    }

    public static ProfileStats createMixedStats() {
        return new ProfileStats(1, 2.3, 2.8, 3.8,
                5.2, 6, 6.8, 8,
                9.1, 10.2, 10.8, 11.8, 13.1, 14.1);
    }

    public static ProfileStats createProfile1EntriesStats() {
        return new ProfileStats(1, 1.7, 1.0, 1.7,
                1.7, 0, 0, 1,
                1, 0, 0, 0, 0, 1.7);
    }


    public static ProfileCustomizations createMixedCustomizations() {
        return new ProfileCustomizations(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 4,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 21, 81, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1});
    }

    public static List<ProfileEntry<?>> createProfile1Entries() {
        //noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
        return Arrays.<ProfileEntry<?>>asList(new ProfileEntry<>(1, 2, 10L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 16, 21L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 102, 32L, 4L, ProfileEntryDataType.Int32, 6L),
                new ProfileEntry<>(2, 103, 43L, 4L, ProfileEntryDataType.Int32, 6L),
                new ProfileEntry<>(2, 104, 54L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 101, 65L, 4L, ProfileEntryDataType.Int32, 12L),
                new ProfileEntry<>(2, 107, 76L, 4L, ProfileEntryDataType.Int32, 10L),
                new ProfileEntry<>(2, 108, 87L, 4L, ProfileEntryDataType.Int32, 10L),
                new ProfileEntry<>(2, 109, 98L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 110, 109L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 111, 120L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 112, 131L, 4L, ProfileEntryDataType.Int32, 7L),
                new ProfileEntry<>(2, 113, 142L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 114, 153L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 115, 164L, 0L, ProfileEntryDataType.String, ""),
                new ProfileEntry<>(2, 116, 175L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 117, 186L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 118, 197L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 119, 208L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 120, 219L, 4L, ProfileEntryDataType.Int32, 100L),
                new ProfileEntry<>(2, 125, 230L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 127, 241L, 4L, ProfileEntryDataType.Int32, 10L),
                new ProfileEntry<>(2, 128, 252L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 129, 263L, 4L, ProfileEntryDataType.Int32, 90L),
                new ProfileEntry<>(2, 130, 274L, 0L, ProfileEntryDataType.Binary, new byte[]{}),
                new ProfileEntry<>(2, 131, 285L, 0L, ProfileEntryDataType.Binary, new byte[]{}),
                new ProfileEntry<>(2, 132, 296L, 0L, ProfileEntryDataType.Binary, new byte[]{}),
                new ProfileEntry<>(2, 133, 307L, 0L, ProfileEntryDataType.Binary, new byte[]{}),
                new ProfileEntry<>(2, 134, 318L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 135, 329L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 140, 340L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 141, 351L, 4L, ProfileEntryDataType.Int32, 100L),
                new ProfileEntry<>(2, 142, 362L, 4L, ProfileEntryDataType.Int32, 100L),
                new ProfileEntry<>(2, 145, 373L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 105, 384L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 147, 395L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 148, 406L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 149, 417L, 0L, ProfileEntryDataType.String, ""),
                new ProfileEntry<>(2, 152, 428L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 153, 439L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 154, 450L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 155, 458L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 156, 466L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 157, 474L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 158, 482L, 0L, ProfileEntryDataType.String, ""),
                new ProfileEntry<>(2, 159, 493L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 160, 501L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 163, 509L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 165, 517L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 166, 525L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 167, 536L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 121, 547L, 4L, ProfileEntryDataType.Int32, 30L),
                new ProfileEntry<>(2, 122, 558L, 4L, ProfileEntryDataType.Int32, 0L),
                new ProfileEntry<>(2, 124, 569L, 4L, ProfileEntryDataType.Int32, 10L),
                new ProfileEntry<>(2, 136, 580L, 4L, ProfileEntryDataType.Int32, 1315L),
                new ProfileEntry<>(2, 137, 591L, 4L, ProfileEntryDataType.Int32, 1315L),
                new ProfileEntry<>(2, 138, 602L, 4L, ProfileEntryDataType.Int32, 10L),
                new ProfileEntry<>(2, 139, 613L, 4L, ProfileEntryDataType.Int32, 22L),
                new ProfileEntry<>(2, 143, 624L, 90L, ProfileEntryDataType.String, "RPMC3DEVJJDM9CBAPH6QD9S6TWP55V8KSPMC3D6VJJDM9CBAPH6HD9S6TCP55V8KSPMC3D6VJJDMSCBAPH6QD9S6T4"),
                new ProfileEntry<>(2, 150, 725L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 300, 736L, 1001L, ProfileEntryDataType.Binary, new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 21, 81, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1}),
                new ProfileEntry<>(2, 151, 1748L, 4L, ProfileEntryDataType.Int32, 4294967295L),
                new ProfileEntry<>(2, 161, 1759L, 1L, ProfileEntryDataType.Int8, 0),
                new ProfileEntry<>(2, 162, 1767L, 9L, ProfileEntryDataType.Binary, new byte[]{-2, 1, 1, 0, 42, 0, -83, 10, 0}),
                new ProfileEntry<>(2, 164, 1787L, 32L, ProfileEntryDataType.String, "MPMC3D2TJJDM9FBAPH67D9S6TWQ55V8K"),
                new ProfileEntry<>(2, 106, 1830L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 169, 1841L, 4L, ProfileEntryDataType.Int32, 1L),
                new ProfileEntry<>(2, 26, 1852L, 4L, ProfileEntryDataType.Int32, 66L),
                new ProfileEntry<>(2, 27, 1863L, 4L, ProfileEntryDataType.Int32, 38L)
        );
    }
}

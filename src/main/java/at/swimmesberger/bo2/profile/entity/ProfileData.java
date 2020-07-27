package at.swimmesberger.bo2.profile.entity;

import at.swimmesberger.bo2.profile.util.ParseUtils;

import java.util.Objects;

public class ProfileData {
    private final int goldenKeys;
    private final ProfileStats stats;
    private final long badassRank;
    private final long badassTokens;
    private final ProfileCustomizations customizations;

    public ProfileData(int goldenKeys, ProfileStats stats, long badassRank, long badassTokens, ProfileCustomizations customizations) {
        this.goldenKeys = goldenKeys;
        this.stats = Objects.requireNonNull(stats);
        this.badassRank = badassRank;
        this.badassTokens = badassTokens;
        this.customizations = Objects.requireNonNull(customizations);
    }

    public static ProfileDataBuilder builder() {
        return new ProfileDataBuilder();
    }

    public static ProfileDataBuilder builder(ProfileData data) {
        return new ProfileDataBuilder(data);
    }

    public long getBadassRank() {
        return badassRank;
    }

    public long getBadassTokens() {
        return badassTokens;
    }

    public int getGoldenKeys() {
        return goldenKeys;
    }

    public ProfileStats getStats() {
        return stats;
    }

    public ProfileCustomizations getCustomizations() {
        return customizations;
    }

    public ProfileData setValue(ProfileDataValueType type, Object value) {
        ProfileDataBuilder builder = builder(this);
        builder.withValue(type, value);
        return builder.build();
    }

    public Object getValue(ProfileDataValueType type) {
        switch (type) {
            case GOLDEN_KEYS:
                return this.getGoldenKeys();
            case BADASS_RANK:
                return this.getBadassRank();
            case BADASS_TOKENS:
                return this.getBadassTokens();
            case ALL_CUSTOMIZATIONS:
                return this.getCustomizations().isAllUnlocked();
            default:
                return this.stats.getValue(type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileData that = (ProfileData) o;
        return goldenKeys == that.goldenKeys &&
                badassRank == that.badassRank &&
                badassTokens == that.badassTokens &&
                Objects.equals(stats, that.stats) &&
                Objects.equals(customizations, that.customizations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goldenKeys, stats, badassRank, badassTokens, customizations);
    }

    @Override
    public String toString() {
        return "ProfileData{" +
                "goldenKeys=" + goldenKeys +
                ", stats=" + stats +
                ", badassRank=" + badassRank +
                ", badassTokens=" + badassTokens +
                ", customizations=" + customizations +
                '}';
    }

    public static final class ProfileDataBuilder {
        private int goldenKeys;
        private ProfileStats.ProfileStatsBuilder stats;
        private long badassRank;
        private long badassTokens;
        private ProfileCustomizations customizations;

        private ProfileDataBuilder() {
            this(null);
        }

        private ProfileDataBuilder(ProfileData profileData) {
            if (profileData != null) {
                this.goldenKeys = profileData.getGoldenKeys();
                this.stats = ProfileStats.builder(profileData.getStats());
                this.badassRank = profileData.getBadassRank();
                this.badassTokens = profileData.getBadassTokens();
                this.customizations = profileData.getCustomizations();
            } else {
                this.stats = ProfileStats.builder();
            }
        }

        public ProfileDataBuilder withGoldenKeys(int goldenKeys) {
            this.goldenKeys = goldenKeys;
            return this;
        }

        public ProfileDataBuilder withStats(ProfileStats stats) {
            this.stats = ProfileStats.builder(stats);
            return this;
        }

        public ProfileDataBuilder withBadassRank(long badassRank) {
            this.badassRank = badassRank;
            return this;
        }

        public ProfileDataBuilder withBadassTokens(long badassTokens) {
            this.badassTokens = badassTokens;
            return this;
        }

        public ProfileDataBuilder withCustomizations(ProfileCustomizations customizations) {
            this.customizations = customizations;
            return this;
        }

        public ProfileDataBuilder withUnlockedCustomizations() {
            if (this.customizations != null) {
                this.customizations = this.customizations.unlockAll();
            } else {
                this.customizations = ProfileCustomizations.allUnlocked();
            }
            return this;
        }

        public ProfileDataBuilder withLockedCustomizations() {
            if (this.customizations != null) {
                this.customizations = this.customizations.lockAll();
            } else {
                this.customizations = ProfileCustomizations.allLocked();
            }
            return this;
        }

        public ProfileDataBuilder withValue(ProfileDataValueType type, Object value) {
            switch (type) {
                case GOLDEN_KEYS:
                    this.withGoldenKeys(ParseUtils.objectToInt(value));
                    break;
                case BADASS_RANK:
                    this.withBadassRank(ParseUtils.objectToLong(value));
                    break;
                case BADASS_TOKENS:
                    this.withBadassTokens(ParseUtils.objectToLong(value));
                    break;
                case ALL_CUSTOMIZATIONS:
                    boolean unlockFlag = ParseUtils.objectToBoolean(value);
                    if (unlockFlag) {
                        this.withUnlockedCustomizations();
                    } else {
                        this.withLockedCustomizations();
                    }
                    break;
                default:
                    this.stats.withValue(type, value);
            }
            return this;
        }

        public ProfileData build() {
            return new ProfileData(goldenKeys, stats.build(), badassRank, badassTokens, customizations);
        }
    }
}

package at.swimmesberger.bo2.profile.entity;

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
}

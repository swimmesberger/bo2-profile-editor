package at.swimmesberger.bo2.profile.entity;

import java.util.Arrays;
import java.util.Objects;

// we have not fully reversed the customizations format but 255 a byte means everything unlocked and 0 nothing unlocked
public class ProfileCustomizations {
    private final int[] customizations;

    public ProfileCustomizations(int[] customizations) {
        this.customizations = Objects.requireNonNull(customizations);
    }

    public int[] getCustomizations() {
        return customizations;
    }

    public ProfileCustomizations unlockAll() {
        int[] copiedCustomizations = new int[this.customizations.length];
        Arrays.fill(copiedCustomizations, 255);
        return new ProfileCustomizations(copiedCustomizations);
    }

    public ProfileCustomizations lockAll() {
        int[] copiedCustomizations = new int[this.customizations.length];
        Arrays.fill(copiedCustomizations, 0);
        return new ProfileCustomizations(copiedCustomizations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileCustomizations that = (ProfileCustomizations) o;
        return Arrays.equals(customizations, that.customizations);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(customizations);
    }

    @Override
    public String toString() {
        return "ProfileCustomizations{" +
                "customizations=" + Arrays.toString(customizations) +
                '}';
    }
}

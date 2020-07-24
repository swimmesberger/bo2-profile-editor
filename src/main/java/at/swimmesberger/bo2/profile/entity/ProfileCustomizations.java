package at.swimmesberger.bo2.profile.entity;

import java.util.Arrays;
import java.util.Objects;

// we have not fully reversed the customizations format but 255 a byte means everything unlocked and 0 nothing unlocked
public class ProfileCustomizations {
    private static final int DEFAULT_CUSTOMIZATION_LENGTH = 1001;
    private static final int UNLOCKED = 255;
    private static final int LOCKED = 0;

    private final int[] customizations;

    public ProfileCustomizations(int[] customizations) {
        this.customizations = Objects.requireNonNull(customizations);
    }

    public static ProfileCustomizations allUnlocked() {
        int[] customizations = new int[DEFAULT_CUSTOMIZATION_LENGTH];
        Arrays.fill(customizations, UNLOCKED);
        return new ProfileCustomizations(customizations);
    }

    public static ProfileCustomizations allLocked() {
        int[] customizations = new int[DEFAULT_CUSTOMIZATION_LENGTH];
        Arrays.fill(customizations, LOCKED);
        return new ProfileCustomizations(customizations);
    }

    public int[] getCustomizations() {
        return customizations;
    }

    public ProfileCustomizations unlockAll() {
        int[] copiedCustomizations = new int[this.customizations.length];
        Arrays.fill(copiedCustomizations, UNLOCKED);
        return new ProfileCustomizations(copiedCustomizations);
    }

    public ProfileCustomizations lockAll() {
        int[] copiedCustomizations = new int[this.customizations.length];
        Arrays.fill(copiedCustomizations, LOCKED);
        return new ProfileCustomizations(copiedCustomizations);
    }

    public boolean isAllLocked() {
        for (int customization : this.customizations) {
            if (customization != LOCKED) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllUnlocked() {
        for (int customization : this.customizations) {
            if (customization != UNLOCKED) {
                return false;
            }
        }
        return true;
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

package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;

class ValueFormatter {

    static String format(ProfileDataValueType type, String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) return rawValue;
        switch (type) {
            case GOLDEN_KEYS:
            case BADASS_RANK:
            case BADASS_TOKENS:
                try {
                    return String.format("%,d", Long.parseLong(rawValue.trim()));
                } catch (NumberFormatException e) {
                    return rawValue;
                }
            case ALL_CUSTOMIZATIONS:
                return rawValue;
            default:
                try {
                    return String.format("%,.1f", Double.parseDouble(rawValue.trim()));
                } catch (NumberFormatException e) {
                    return rawValue;
                }
        }
    }
}

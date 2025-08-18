package espresso.achievement.domain.constants;

import java.util.Set;

/**
 * Constants related to achievement skills.
 * This class centralizes skill-related constants for use across all layers of the achievement module.
 */
public final class AchievementConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AchievementConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Set of allowed skill abbreviations.
     * These represent the valid skill types that can be associated with achievements.
     */
    public static final Set<String> ALLOWED_SKILLS = Set.of("str", "dex", "con", "wis", "int", "cha", "luc");

    /**
     * Error message template for invalid skill validation.
     * Use with String.format to include the invalid skill value.
     */
    public static final String ERROR_INVALID_SKILL = "LOCALIZE: INVALID SKILL '%s'. ALLOWED SKILLS ARE: str, dex, con, wis, int, cha, luc";
}

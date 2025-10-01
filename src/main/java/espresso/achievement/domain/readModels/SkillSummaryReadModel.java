package espresso.achievement.domain.readModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Read model for skill summary information.
 * Contains minimal skill data for display in lists and dropdowns.
 * Part of the CQRS pattern for optimized data reading.
 */
@Getter
@AllArgsConstructor
public class SkillSummaryReadModel {
    
    // private final String name;
    
    /**
     * The standardized abbreviation code for the skill (e.g., "java", "react", "aws").
     */
    private final String abbreviation;
}

package espresso.achievement.domain.entities;

import lombok.Data;
 

/**
 * Value object representing a skill that can be associated with achievements.
 * Contains skill information including abbreviation, name, and description.
 */
@Data
public class Skill {
   
    /**
     * The 3-character abbreviation for the skill (e.g., "str", "dex", "con").
     */
    private String abbreviation;
    
    /**
     * The full name of the skill (e.g., "strength", "dexterity", "constitution").
     */
    private String name;
    
    /**
     * A detailed description of what the skill represents.
     */
    private String description;

    /**
     * Constructor to create a new Skill instance.
     * 
     * @param name The full name of the skill
     * @param abbreviation The 3-character abbreviation
     * @param description The detailed description
     */
    public Skill(String name, String abbreviation, String description) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;
    }

    /**
     * Removes the description field for serialization optimization.
     * Used when sending skill data in responses where description is not needed.
     */
    public void cleanForSerialization() {
        this.description = null;
    }
}

package espresso.achievement.cmd.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Skill extends Entity {
   
    @Getter
    String name;

    @Getter
    String abbreviation;

    @Getter
    String description;

    public Skill(String name, String abbreviation, String description) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;

        // Set the key to the same value as the abbreviation, but in lowercase and with four trailing zeros
        setKey(abbreviation.toLowerCase() + "0000");
    }
}

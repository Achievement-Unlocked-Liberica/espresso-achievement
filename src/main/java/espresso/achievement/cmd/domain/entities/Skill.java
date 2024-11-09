package espresso.achievement.cmd.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Skill extends Entity {
   

    private String name;
    private String abbreviation;
    private String description;

    public Skill(String name, String abbreviation, String description) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;

        // Set the key to the same value as the abbreviation, but in lowercase and with four trailing zeros
        setKey(abbreviation.toLowerCase()+"0000");
    }

    public void cleanForSerialization() {
        this.setId(null);
        this.setTimestamp(null);
        this.description = null;
    }
}

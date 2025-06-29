package espresso.achievement.domain.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
public class Skill {
   
    private String abbreviation;
    private String name;
    private String description;

    public Skill(String name, String abbreviation, String description) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;
    }

    public void cleanForSerialization() {
        this.description = null;
    }
}

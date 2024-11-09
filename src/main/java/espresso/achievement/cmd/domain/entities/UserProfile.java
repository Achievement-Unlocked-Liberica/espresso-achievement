package espresso.achievement.cmd.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserProfile extends Entity{
    
    @Getter
    String userName;

    @Getter
    String firstName;

    @Getter
    String lastName;

    @Getter
    String email;

    public void cleanForSerialization() {
        this.setId(null);
        this.setTimestamp(null);

        this.firstName = null;
        this.lastName = null;
    }
}

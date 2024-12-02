package espresso.achievement.domain.entities;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserProfile extends Entity {

    @Indexed(name = "user_profile_idx",  unique = false)
    @Setter
    private String key;

    String userName;
    String firstName;
    String lastName;
    String email;

    public void cleanForSerialization() {
        this.setId(null);
        this.setTimestamp(null);

        this.firstName = null;
        this.lastName = null;
    }
}

package espresso.achievement.domain.readModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDetailReadModel {
    private final String key;
    private final String userName;
    private final String email;
    private final String firstName;
    private final String lastName;
}
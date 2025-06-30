package espresso.user.domain.events;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewUserCreated {
    private final String userKey;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
}

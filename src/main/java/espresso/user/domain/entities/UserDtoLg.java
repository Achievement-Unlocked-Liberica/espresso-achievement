package espresso.user.domain.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface UserDtoLg {

    String getEntityKey();

    String getUsername();

    String getEmail();

    String getFirstName();

    String getLastName();

    LocalDate getBirthDate();

    String getProfilePictureUrl();

    boolean isEmailVerified();

    boolean isActive();

    OffsetDateTime getRegisteredAt();
}

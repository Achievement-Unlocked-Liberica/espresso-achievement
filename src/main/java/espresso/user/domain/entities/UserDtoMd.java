package espresso.user.domain.entities;

import java.time.LocalDate;

public interface UserDtoMd {

    String getEntityKey();

    String getUsername();

    String getEmail();

    String getFirstName();

    String getLastName();

    LocalDate getBirthDate();
}

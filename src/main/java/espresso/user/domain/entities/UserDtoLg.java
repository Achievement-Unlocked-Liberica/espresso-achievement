package espresso.user.domain.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface UserDtoLg {

    String getEntityKey();

    String getUsername();

    String getEmail();

    String getFirstName();

    String getLastName();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getBirthDate();

    UserProfileImageDtoLg getProfileImage();

    boolean isEmailVerified();

    boolean isActive();

    OffsetDateTime getRegisteredAt();
}

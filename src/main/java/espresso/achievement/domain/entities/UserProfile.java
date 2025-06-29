package espresso.achievement.domain.entities;

import jakarta.persistence.Entity;
import espresso.common.domain.models.DomainEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@ToString
// @Entity(name = "UserProfile")
// @Table(name = "UserProfiles")
public class UserProfile extends DomainEntity {

    String userName;
    String firstName;
    String lastName;
    String email;

    public void cleanForSerialization() {
        this.setId(null);
        this.setTimeStamp(null);

        this.firstName = null;
        this.lastName = null;
    }
}

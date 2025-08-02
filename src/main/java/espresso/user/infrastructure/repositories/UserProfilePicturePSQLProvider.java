package espresso.user.infrastructure.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import espresso.user.domain.entities.UserProfileImage;

@Repository
public interface UserProfilePicturePSQLProvider extends JpaRepository<UserProfileImage, Long>{

}

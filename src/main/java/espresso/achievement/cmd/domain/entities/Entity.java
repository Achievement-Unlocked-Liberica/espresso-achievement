package espresso.achievement.cmd.domain.entities;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class Entity {
    
    @Id
    @Getter
    @Setter
    private UUID id;

    @Indexed(unique = true)
    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private Date timestamp;   
}



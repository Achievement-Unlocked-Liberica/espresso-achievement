package espresso.user.domain.entities;

// A Key Transfer Object (KTO) for User entity to expose only essential fields
// without loading the entire entity, useful for lightweight references.
public interface UserKto {

    Long getId();

    String getEntityKey();
}

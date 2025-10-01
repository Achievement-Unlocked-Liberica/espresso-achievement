package espresso.common.domain.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for value entities in the domain model.
 * Value entities are objects that are defined by their attributes rather than their identity.
 * They are typically immutable and do not have an independent lifecycle.
 * Examples include comments, celebrations, and media records that belong to aggregates.
 */
@Getter
@Setter
public class ValueEntity {

}

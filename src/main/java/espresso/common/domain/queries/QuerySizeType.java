package espresso.common.domain.queries;

/**
 * Enumeration defining the size levels for query response DTOs.
 * Controls the amount of detail included in query responses to optimize data transfer.
 * Larger sizes include more fields and nested objects, while smaller sizes contain minimal data.
 */
public enum QuerySizeType {
    /** Extra small - minimal essential data only */
    xs(1), 
    
    /** Small - basic information for list views */
    sm(2), 
    
    /** Medium - moderate detail for card views */
    md(3), 
    
    /** Large - comprehensive detail for full views */
    lg(4), 
    
    /** Extra large - complete data including all relationships */
    xl(5);

    /**
     * Numeric value representing the size level for ordering and comparison.
     */
    private final int value;

    /**
     * Creates a QuerySizeType with the specified numeric value.
     * 
     * @param value The numeric representation of the size level
     */
    QuerySizeType(int value) {
        this.value = value;
    }

    /**
     * Gets the numeric value of this size type.
     * 
     * @return The numeric value representing the size level
     */
    public int getValue() {
        return value;
    }
}

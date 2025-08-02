package espresso.common.domain.queries;

public enum QuerySizeType {
    xs(1), 
    sm(2), 
    md(3), 
    lg(4), 
    xl(5);

    private final int value;

    QuerySizeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

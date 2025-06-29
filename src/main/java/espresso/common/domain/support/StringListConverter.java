package espresso.common.domain.support;

    import jakarta.persistence.AttributeConverter;
    import jakarta.persistence.Converter;
    import java.util.Arrays;
    import java.util.List;
    import static java.util.Collections.emptyList;

    @Converter
    public class StringListConverter implements AttributeConverter<List<String>, String> {

        private static final String SPLIT_CHAR = ";";

        @Override
        public String convertToDatabaseColumn(List<String> stringList) {
            return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            return dbData != null && !dbData.isEmpty() ? Arrays.asList(dbData.split(SPLIT_CHAR)) : emptyList();
        }
    }
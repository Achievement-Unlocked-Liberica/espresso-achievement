package espresso.common.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HandlerResponse<T> {

    private boolean success;
    @Builder.Default
    private Integer count = null;
    private T data;
    private ResponseType responseType;

    public static <T> HandlerResponse<T> empty() {
        return HandlerResponse.<T>builder()
                .data(null)
                .responseType(ResponseType.NONE)
                .success(true)
                .build();
    }

    public static <T> HandlerResponse<T> noContent() {
        return HandlerResponse.<T>builder()
                .data(null)
                .responseType(ResponseType.NO_CONTENT)
                .success(true)
                .build();
    }

    public static <T> HandlerResponse<T> created(T data) {

        Integer count = null;
        if (data instanceof java.util.List<?>) {
            count = ((java.util.List<?>) data).size();
        }

        return HandlerResponse.<T>builder()
                .data(data)
                .count(count)
                .responseType(ResponseType.CREATED)
                .success(true)
                .build();
    }

    public static <T> HandlerResponse<T> success(T data) {

        Integer count = null;

        if (data instanceof java.util.List<?>) {
            count = ((java.util.List<?>) data).size();
        }

        return HandlerResponse.<T>builder()
                .data(data)
                .count(count)
                .responseType(ResponseType.SUCCESS)
                .success(true)
                .build();

    }

    public static <T> HandlerResponse<T> error(T data, ResponseType responseType) {
        return HandlerResponse.<T>builder()
                .data(data)
                .responseType(responseType)
                .success(false)
                .build();
    }

    public boolean HasData() {
        return data != null;
    }
}

package espresso.common.domain.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HandlerResponseList<T> {

    private boolean success;
    private int count;
    private T[] data;
    private T[] errors;
    private ResponseType responseType;

    public static <T> HandlerResponseList<T> empty() {
        return HandlerResponseList.<T>builder()
                .data(null)
                .count(0)
                .responseType(ResponseType.NONE)
                .success(true)
                .build();
    }

    public static <T> HandlerResponseList<T> created(T[] data) {
        return HandlerResponseList.<T>builder()
                .data(data)
                .count(data != null ? data.length : 0)
                .responseType(ResponseType.CREATED)
                .success(true)
                .build();
    }

    public static <T> HandlerResponseList<T> success(T[] data) {
        return HandlerResponseList.<T>builder()
                .data(data)
                .count(data != null ? data.length : 0)
                .responseType(ResponseType.SUCCESS)
                .success(true)
                .build();
    }

    public static <T> HandlerResponseList<T> error(T[] errors, ResponseType responseType) {
        return HandlerResponseList.<T>builder()
                .errors(errors)
                .responseType(responseType)
                .success(false)
                .build();
    }

    public boolean hasData() {
        return data != null && data.length > 0;
    }

}

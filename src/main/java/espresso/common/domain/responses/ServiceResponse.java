package espresso.common.domain.responses;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ServiceResponse<T> {

    private boolean success;
    @Builder.Default
    private Integer count = null;
    private T data;
    private HttpStatus httpStatus;

    public static <T> ServiceResponse<T> empty() {
        return success(null, null, null);
    }

    public static <T> ServiceResponse<T> success(HttpStatus httpStatus, T data, Integer count) {
        return ServiceResponse.<T>builder()
            .data(data)
            .count(count)
            .success(true)
            .httpStatus(httpStatus)
            .build();
    }

    public static <T> ServiceResponse<T> error(HttpStatus httpStatus, T data) {
        return ServiceResponse.<T>builder()
            .data(data)
            .success(false)
            .httpStatus(httpStatus)
            .build();
    }
}

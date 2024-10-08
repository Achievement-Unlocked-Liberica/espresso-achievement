package espresso.achievement.cmd.service.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResult<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResult<T> empty() {
        return success(null, null);
    }

    public static <T> ApiResult<T> success(T data, String message) {
        return ApiResult.<T>builder()
        .message(message != null ? message : "SUCCESS!")
        .data(data)
        .success(true)
        .build();
    }

    public static <T> ApiResult<T> error(String message) {
        return ApiResult.<T>builder()
        .message(message != null ? message :"ERROR!")
        .success(false)
        .build();
    }
}

package espresso.achievement.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HandlerResult<T> {

        private boolean success;
        private String message;
        private T data;
    
        public static <T> HandlerResult<T> empty() {
            return success(null, null);
        }
    
        public static <T> HandlerResult<T> success(String message, T data) {
            return HandlerResult.<T>builder()
            .message(message != null ? message : "SUCCESS!")
            .data(data)
            .success(true)
            .build();
        }
    
        public static <T> HandlerResult<T> error(String message, T data) {
            return HandlerResult.<T>builder()
            .message(message != null ? message :"ERROR!")
            .data(data)
            .success(false)
            .build();
        }

        public boolean HasData() {
            return data != null;
        }
    }

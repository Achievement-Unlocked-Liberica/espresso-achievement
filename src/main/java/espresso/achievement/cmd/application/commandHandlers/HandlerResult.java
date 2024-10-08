package espresso.achievement.cmd.application.commandHandlers;

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
    
        public static <T> HandlerResult<T> success(T data, String message) {
            return HandlerResult.<T>builder()
            .message(message != null ? message : "SUCCESS!")
            .data(data)
            .success(true)
            .build();
        }
    
        public static <T> HandlerResult<T> error(String message) {
            return HandlerResult.<T>builder()
            .message(message != null ? message :"ERROR!")
            .success(false)
            .build();
        }
    }

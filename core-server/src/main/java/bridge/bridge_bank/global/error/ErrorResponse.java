package bridge.bridge_bank.global.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, path, LocalDateTime.now());
    }
}

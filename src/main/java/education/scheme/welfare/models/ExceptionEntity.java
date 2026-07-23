package education.scheme.welfare.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExceptionEntity {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
package cristiancicale.G5S2U5.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorsPayload {
    private String message;
    private LocalDateTime timestamp;
}

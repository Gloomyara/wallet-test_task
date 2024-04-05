package ru.antonovmikhail.util.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.antonovmikhail.util.Constants.DATE_TIME_PATTERN;


@Data
@Builder
public class ErrorResponse {
    private HttpStatus status;
    private String reason;
    private String message;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timestamp;
}

package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class UrlCheck {
    private Long id;
    private Long urlId;
    private Integer statusCode;
    private String h1;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public UrlCheck(Long urlId, Integer statusCode, String h1, String title, String description) {
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.h1 = h1;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}

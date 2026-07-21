package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Url {
    private Long id;

    private String name;
    private LocalDateTime createdAt;

    public Url(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Url(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}

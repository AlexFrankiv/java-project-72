package hexlet.code.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UrlsPage extends BasePage {
    private String error;
    private String url;
}

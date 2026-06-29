package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;

@Getter
public class UrlShowPage extends BasePage {
    private final Url url;

    public UrlShowPage(Url url, String flash) {
        super(flash);
        this.url = url;
    }
}

package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;

import java.util.List;

@Getter
public class UrlsCheckPage extends BasePage {
    private final Url url;
    private final List<UrlCheck> checks;

    public UrlsCheckPage(Url url, List<UrlCheck> checks) {
        this.url = url;
        this.checks = checks;
    }
}

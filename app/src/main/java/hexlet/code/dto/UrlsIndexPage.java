package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;

import java.util.List;

@Getter
public class UrlsIndexPage extends BasePage {
    private final List<Url> urls;

    public UrlsIndexPage(List<Url> urls, String flash) {
        super(flash);
        this.urls = urls;
    }
}

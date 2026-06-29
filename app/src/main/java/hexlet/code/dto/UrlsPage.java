package hexlet.code.dto;

import lombok.Getter;


@Getter
public class UrlsPage extends BasePage {
    private String error;   // сообщение об ошибке
    private String url;

    public UrlsPage(String error, String url, String flash) {
        super(flash);
        this.error = error;
        this.url = url;
    }
    public UrlsPage(String error, String url) {
        this(error, url, null);
    }
}

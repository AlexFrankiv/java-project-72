package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class UrlsIndexPage extends BasePage {
    private final List<Url> urls;
    private final Map<Long, UrlCheck> lastChecks;


}

package hexlet.code.controllers;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.sql.SQLException;

public class UrlChecksController {
    public static void check(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL не найден"));

        try {
            var response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();
            String body = response.getBody();

            if (statusCode >= 400) {
                ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
                ctx.redirect(NamedRoutes.urlPath(id));
                return;
            }

            var doc = Jsoup.parse(body);
            String h1 = doc.select("h1").first() != null ? doc.select("h1").first().text() : "";
            String title = doc.title();
            String description = doc.select("meta[name=description]").first() != null
                    ? doc.select("meta[name=description]").first().attr("content") : "";

            h1 = truncate(h1, 200);
            title = truncate(title, 200);
            description = truncate(description, 200);

            var check = new UrlCheck(id, statusCode, h1, title, description);
            UrlCheckRepository.save(check);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.redirect(NamedRoutes.urlPath(id));

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}

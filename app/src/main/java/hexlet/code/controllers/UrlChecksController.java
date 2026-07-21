package hexlet.code.controllers;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.UrlUtils;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.sql.SQLException;


@Slf4j
public class UrlChecksController {
    public static void check(Context ctx) throws SQLException {
        Long id;
        try {
            id = ctx.pathParamAsClass("id", Long.class).get();
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Неверный идентификатор");
            return;
        }

        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL не найден"));

        try {
            var response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();

            if (statusCode >= HttpStatus.BAD_REQUEST.getCode()) {
                UrlUtils.alertFlash(ctx, "Произошла ошибка при проверке", "danger");
                ctx.redirect(NamedRoutes.urlPath(id));
                return;
            }

            String body = response.getBody();
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

            UrlUtils.alertFlash(ctx, "Страница успешно проверена", "success");
            ctx.redirect(NamedRoutes.urlPath(id));
            log.info("Проверка URL {} завершена успешно", url.getName());

        } catch (Exception e) {
            log.error("Ошибка при проверке URL id={}", id, e);
            UrlUtils.alertFlash(ctx, "Произошла ошибка при проверке", "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}

package hexlet.code.controllers;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.UrlUtils;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UrlChecksController {
    private static final Logger log = LoggerFactory.getLogger(UrlChecksController.class);

    public static void check(Context ctx) {
        Long id;
        try {
            id = ctx.pathParamAsClass("id", Long.class).get();
        } catch (Exception e) {
            ctx.status(404).result("Неверный идентификатор");
            return;
        }

        try {
            var url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("URL не найден"));

            var response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();
            String body = response.getBody();

            if (statusCode >= 400) {
                UrlUtils.alertFlash(ctx, "Произошла ошибка при проверке", "danger");
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

            UrlUtils.alertFlash(ctx, "Страница успешно проверена", "success");
            ctx.redirect(NamedRoutes.urlPath(id));
            log.info("Проверка URL {} завершена успешно", url.getName());

        } catch (NotFoundResponse e) {
            ctx.status(404).result("URL не найден");
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

package hexlet.code.controllers;

import hexlet.code.dto.UrlsCheckPage;
import hexlet.code.dto.UrlsIndexPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.UrlUtils;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class UrlsController {
    public static void create(Context ctx) throws SQLException, URISyntaxException {
        String rawUrl = ctx.formParam("url");
        if (rawUrl == null || rawUrl.isBlank()) {
            var page = new UrlsPage("URL не может быть пустым", rawUrl);
            ctx.render("index.jte", model("page", page)).status(422);
            return;
        }

        String normalized;
        try {
            normalized = UrlUtils.normalizeUrl(rawUrl);
        } catch (URISyntaxException e) {
            var page = new UrlsPage("Некорректный URL", rawUrl);
            ctx.render("index.jte", model("page", page)).status(422);
            log.error("Ошибка валидации URL: {}", rawUrl, e);
            return;
        }

        String domain = UrlUtils.extractDomain(normalized);

        var existing = UrlRepository.findByName(domain);
        if (existing.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlPath(existing.get().getId()));
            return;
        }

        var newUrl = new Url(domain);
        UrlRepository.save(newUrl);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.urlPath(newUrl.getId()));
        log.info("Страница успешно добавлена: {}", rawUrl);
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var lastChecks = UrlCheckRepository.findLatestChecks();
        var page = new UrlsIndexPage(urls, lastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL не найден"));
        var checks = UrlCheckRepository.findByUrlId(id);
        var page = new UrlsCheckPage(url, checks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/show.jte", model("page", page));
    }
}

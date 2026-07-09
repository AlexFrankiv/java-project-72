package hexlet.code.controllers;

import hexlet.code.dto.UrlsCheckPage;
import hexlet.code.dto.UrlsIndexPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        Map<Long, UrlCheck> lastChecks = new HashMap<>();
        for (Url url : urls) {
            UrlCheck lastCheck = UrlCheckRepository.findLastByUrlId(url.getId());
            if (lastCheck != null) {
                lastChecks.put(url.getId(), lastCheck);
            }
        }
        var page = new UrlsIndexPage(urls, lastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void create(Context ctx) {
        String rawUrl = ctx.formParam("url");
        if (rawUrl == null || rawUrl.isBlank()) {
            var page = new UrlsPage("Некорректный URL", rawUrl);
            ctx.render("index.jte", model("page", page)).status(422);
            return;
        }

        String normalized = rawUrl.trim();
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }

        try {
            var urlObj = new URI(normalized).toURL();
            String protocol = urlObj.getProtocol();
            String host = urlObj.getHost();
            int port = urlObj.getPort();

            if (protocol == null || host == null || !(host.contains(".") || host.equals("localhost") || host.equals("127.0.0.1"))) {
                throw new Exception("Invalid URL");
            }

            String domain = protocol.toLowerCase() + "://" + host.toLowerCase();
            if (port > 0) {
                domain += ":" + port;
            }

            var existing = UrlRepository.findByName(domain);
            if (existing.isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.redirect(NamedRoutes.urlPath(existing.get().getId()));
                return;
            }

            var newUrl = new Url(domain);
            UrlRepository.save(newUrl);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.redirect(NamedRoutes.urlPath(newUrl.getId()));

        } catch (Exception e) {
            var page = new UrlsPage("Некорректный URL", rawUrl);
            ctx.render("index.jte", model("page", page)).status(422);
        }
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL не найден"));
        var checks = UrlCheckRepository.findByUrlId(id);
        var page = new UrlsCheckPage(url, checks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/show.jte", model("page", page));
    }
}

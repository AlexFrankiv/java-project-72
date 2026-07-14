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
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import org.slf4j.Logger;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    private static final Logger log = LoggerFactory.getLogger(UrlsController.class);

    public static void create(Context ctx) {
        String rawUrl = ctx.formParam("url");

        try {
            if (rawUrl == null || rawUrl.isBlank()) {
                UrlUtils.alertFlash(ctx, "URL не может быть пустым", "danger");
                ctx.render(NamedRoutes.rootPath()).status(422);
                return;
            }

            createUrl(rawUrl);

            UrlUtils.alertFlash(ctx, "Страница успешно добавлена", "success");
            ctx.redirect(NamedRoutes.urlsPath());
            log.info("Страница успешно добавлена: {}", rawUrl);

        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            var page = new UrlsPage("Некорректный URL", rawUrl);
            ctx.render("index.jte", model("page", page)).status(422);
            log.error("Ошибка валидации URL: {}", rawUrl, e);

        } catch (SQLDataException e) {
            UrlUtils.alertFlash(ctx, "Страница уже существует", "danger");
            ctx.redirect(NamedRoutes.urlsPath());
            log.warn("Страница уже существует: {}", rawUrl);

        } catch (SQLException e) {
            log.error("Ошибка базы данных при добавлении URL: {}", rawUrl, e);
            ctx.status(500).result("Внутренняя ошибка сервера");
        }
    }

    public static void createUrl(String rawUrl) throws URISyntaxException, MalformedURLException, SQLException {
        String normalized = UrlUtils.normalizeUrl(rawUrl);
        String domain = UrlUtils.extractDomain(normalized);

        if (UrlRepository.findByName(domain).isPresent()) {
            throw new SQLDataException("Страница уже существует: " + domain);
        }

        var url = new Url(domain);
        UrlRepository.save(url);
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

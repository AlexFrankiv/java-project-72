package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.dto.UrlShowPage;
import hexlet.code.dto.UrlsIndexPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }
    private static String getDatabaseUrl() {
        // Получаем url базы данных из переменной окружения DATABASE_URL
        // Если она не установлена, используем базу в памяти
        return System.getenv().getOrDefault("DATABASE_URL", "jdbc:h2:mem:project");
    }

    private static void initDatabase() throws Exception {
        String dbUrl = getDatabaseUrl();
        String dbUser = System.getenv().getOrDefault("USERNAME", "");
        String dbPassword = System.getenv().getOrDefault("PASSWORD", "");

        var config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        if (!dbUser.isEmpty()) {
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
        }
        config.setMaximumPoolSize(10);

        var dataSource = new HikariDataSource(config);
        BaseRepository.dataSource = dataSource;

        // Инициализация схемы
        var schemaStream = App.class.getClassLoader().getResourceAsStream("schema.sql");
        if (schemaStream == null) {
            throw new RuntimeException("schema.sql not found");
        }
        var sql = new BufferedReader(new InputStreamReader(schemaStream))
                .lines().collect(Collectors.joining("\n"));
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static Javalin getApp() {
        if (BaseRepository.dataSource == null) {
            try {
                initDatabase();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка инициализации БД", e);
            }
        }
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        app.get("/", ctx -> {
            var page = new UrlsPage(null, null);
            ctx.render("index.jte", model("page", page));
        });
        app.post("/urls", ctx -> {
            var currentUrl = ctx.formParam("url");
            if (currentUrl.isBlank()) {
                var page = new UrlsPage("Некорректный URL", currentUrl);
                ctx.render("index.jte", model("page", page)).status(422);
                return;
            }

            var lowerCaseCurrentURL = currentUrl.toLowerCase();
            if (!lowerCaseCurrentURL.startsWith("https://") && !lowerCaseCurrentURL.startsWith("http://")) {
                currentUrl = "http://" + currentUrl;
            }

            try {
                var instanceURL = new URI(currentUrl).toURL();
                String protocolURl = instanceURL.getProtocol();
                String hostURl = instanceURL.getHost();
                int portURl = instanceURL.getPort();

                if (protocolURl == null || hostURl == null  || !hostURl.contains(".")) {
                    throw new Exception("Invalid URL");
                }

                var lowerProtocol = protocolURl.toLowerCase();
                var lowerHost = hostURl.toLowerCase();
                String normalizedDomain = lowerProtocol + "://" + lowerHost;
                if (portURl > 0) {
                    normalizedDomain += ":" + portURl;
                }

                var existing = UrlRepository.findByName(normalizedDomain);
                if (existing.isPresent()) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.redirect("/urls/" + existing.get().getId());
                    return;
                }
                var newUrl = new Url(normalizedDomain);
                UrlRepository.save(newUrl);

                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.redirect("/urls/" + newUrl.getId());

            } catch (Exception e) {
                e.printStackTrace();
                var page = new UrlsPage("Некорректный URL", currentUrl);
                ctx.render("index.jte", model("page", page)).status(422);
            }

        });
        app.get("/urls", ctx -> {
            try {
                String flash = ctx.consumeSessionAttribute("flash");
                var urls = UrlRepository.getEntities();
                var page = new UrlsIndexPage(urls, flash);
                ctx.render("urls/index.jte", model("page", page));
            } catch (SQLException e) {
                ctx.status(500).result("Ошибка базы данных");
            }
        });
        app.get("/urls/{id}", ctx -> {
            try {
                Long id = ctx.pathParamAsClass("id", Long.class).get();
                var url = UrlRepository.find(id)
                        .orElseThrow(() -> new NotFoundResponse("URL не найден"));
                String flash = ctx.consumeSessionAttribute("flash");
                var page = new UrlShowPage(url, flash);
                ctx.render("urls/show.jte", model("page", page));
            } catch (SQLException e) {
                ctx.status(500).result("Ошибка базы данных");
            }
        });

        return app;
    }
    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static void main(String[] args) {
        getApp().start(getPort());
    }
}

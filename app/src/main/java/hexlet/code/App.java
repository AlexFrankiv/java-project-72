package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.util.Map;

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

    public static Javalin getApp() {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        app.get("/", ctx ->
                ctx.render("layout.jte", Map.of("title", "Главная", "page", "index")));
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

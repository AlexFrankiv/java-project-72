package hexlet.code;

import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlChecksController;
import hexlet.code.controllers.UrlsController;
import hexlet.code.utils.DataBaseInitialization;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.Template;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() throws Exception {
        DataBaseInitialization.initDatabase();
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(Template.createTemplateEngine()));
        });

        app.get(NamedRoutes.rootPath(), RootController::index);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlPathChecks("{id}"), UrlChecksController::check);

        return app;
    }


    public static void main(String[] args) throws Exception {
        Javalin app = getApp();
        app.start(getPort());
    }
}

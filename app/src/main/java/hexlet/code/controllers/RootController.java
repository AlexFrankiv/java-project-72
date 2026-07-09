package hexlet.code.controllers;

import hexlet.code.dto.UrlsPage;
import io.javalin.http.Context;

import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {
    public static void index(Context ctx) {
        var page = new UrlsPage(null, null);
        ctx.render("index.jte", model("page", page));
    }
}

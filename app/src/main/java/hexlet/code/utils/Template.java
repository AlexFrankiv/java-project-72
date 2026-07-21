package hexlet.code.utils;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;


import java.nio.file.Path;

public class Template {
    public static TemplateEngine createTemplateEngine() {
        var targetDirectory = Path.of("jte-classes");
        return TemplateEngine.createPrecompiled(targetDirectory, ContentType.Html);
    }
}

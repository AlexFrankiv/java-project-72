package hexlet.code.utils;

import io.javalin.http.Context;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {
    public static void alertFlash(Context ctx, String message, String type) {
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flash-type", type);
    }

    public static String normalizeUrl(String rawUrl) throws URISyntaxException {
        String normalized = rawUrl.trim();
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }

        var uri = new URI(normalized);
        String host = uri.getHost();
        if (host == null || !(host.contains(".") || host.equals("localhost") || host.equals("127.0.0.1"))) {
            throw new URISyntaxException(normalized, "Invalid host");
        }
        return normalized;
    }

    public static String extractDomain(String url) throws URISyntaxException {
        var uri = new URI(url);
        String protocol = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        if (protocol == null || host == null) {
            throw new URISyntaxException(url, "Missing protocol or host");
        }
        String domain = protocol.toLowerCase() + "://" + host.toLowerCase();
        if (port > 0) {
            domain += ":" + port;
        }
        return domain;
    }
}

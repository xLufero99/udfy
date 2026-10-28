package co.edu.udistrital.udfy.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class YoutubeIdExtractor {
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{11}$");

    public String extract(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Link de YouTube inválido");
        }
        try {
            URI uri = new URI(url.trim());
            String host = uri.getHost();
            if (host == null) {
                throw new IllegalArgumentException("Link de YouTube inválido");
            }
            host = host.toLowerCase();
            if (!isValidHost(host)) {
                throw new IllegalArgumentException("Link de YouTube inválido");
            }
            if ("youtu.be".equals(host)) {
                String path = uri.getPath();
                if (path == null || path.isBlank()) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                String id = path.startsWith("/") ? path.substring(1) : path;
                if (id.contains("/")) {
                    id = id.split("/")[0];
                }
                if (!ID_PATTERN.matcher(id).matches()) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                return id;
            }
            String path = uri.getPath();
            if (path == null) {
                throw new IllegalArgumentException("Link de YouTube inválido");
            }
            if (path.startsWith("/watch")) {
                String query = uri.getQuery();
                if (query == null || query.isBlank()) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2 && "v".equals(kv[0])) {
                        String id = kv[1];
                        if (!ID_PATTERN.matcher(id).matches()) {
                            throw new IllegalArgumentException("Link de YouTube inválido");
                        }
                        return id;
                    }
                }
                throw new IllegalArgumentException("Link de YouTube inválido");
            } else if (path.startsWith("/shorts/")) {
                String[] parts = path.split("/");
                if (parts.length < 3) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                String id = parts[2];
                if (!ID_PATTERN.matcher(id).matches()) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                return id;
            } else if (path.startsWith("/embed/")) {
                String[] parts = path.split("/");
                if (parts.length < 3) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                String id = parts[2];
                if (!ID_PATTERN.matcher(id).matches()) {
                    throw new IllegalArgumentException("Link de YouTube inválido");
                }
                return id;
            }
            throw new IllegalArgumentException("Link de YouTube inválido");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Link de YouTube inválido");
        }
    }

    private boolean isValidHost(String host) {
        return "youtube.com".equals(host) || "www.youtube.com".equals(host)
                || "m.youtube.com".equals(host) || "music.youtube.com".equals(host)
                || "youtu.be".equals(host);
    }
}

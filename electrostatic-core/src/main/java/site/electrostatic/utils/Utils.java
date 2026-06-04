package site.electrostatic.utils;

import site.electrostatic.core.GenerationOptionsContext;
import site.electrostatic.site.Site;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Utils {
    public static String relativeUrl(String url) {
        if (url == null || url.isBlank() || !url.startsWith("/") || isAbsoluteUrl(url)) {
            return url;
        }

        String basePath = basePathFromConfiguredBaseUrl();
        if (basePath.isEmpty() || "/".equals(basePath)) {
            return url;
        }

        if (url.startsWith(basePath + "/") || url.equals(basePath) || ("/".equals(url) && (basePath + "/").equals(url))) {
            return url;
        }

        if ("/".equals(url)) {
            return basePath + "/";
        }

        return basePath + url;
    }

    private static boolean isAbsoluteUrl(String url) {
        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.startsWith("http://")
            || lowerCaseUrl.startsWith("https://")
            || lowerCaseUrl.startsWith("//");
    }

    private static String basePathFromConfiguredBaseUrl() {
        String baseUrl = GenerationOptionsContext.current().baseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return "";
        }

        try {
            String path = URI.create(baseUrl).getPath();
            if (path == null || path.isBlank() || "/".equals(path)) {
                return "";
            }

            String normalizedPath = path;
            while (normalizedPath.endsWith("/") && normalizedPath.length() > 1) {
                normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
            }

            return normalizedPath.startsWith("/") ? normalizedPath : "/" + normalizedPath;
        } catch (IllegalArgumentException ignored) {
            return "";
        }
    }

    public static String escape(String title) {
        return title;
    }

    public static String absoluteUrl(String path) {
        return path;
    }

    /**
     * Resolves an image URL to an absolute URL.
     * If the imageUrl is null, returns null.
     * If the imageUrl is already absolute (starts with http://, https://, or //), returns it as-is.
     * Otherwise, uses the site's resolveUrl method to convert it to an absolute URL.
     *
     * @param imageUrl the image URL to resolve (may be relative or absolute)
     * @param site the site configuration containing the base URL
     * @return the absolute image URL, or null if imageUrl is null
     */
    public static String resolveImageUrl(String imageUrl, Site site) {
        if (imageUrl == null) {
            return null;
        }
        
        // If already absolute or protocol-relative, use as-is; otherwise resolve to absolute URL
        String lowerCaseUrl = imageUrl.toLowerCase();
        if (lowerCaseUrl.startsWith("http://") || lowerCaseUrl.startsWith("https://") || lowerCaseUrl.startsWith("//")) {
            return imageUrl;
        } else {
            return site.resolveUrl(imageUrl);
        }
    }

    public static String format(LocalDate date) {
        if(date == null) return null;
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
    }

    public static String capitalize(String category) {
        return StringUtils.capitalize(category);
    }

    public static String urlFromKey(String key) {
        String[] parts = key.split("-", 4);
        if (parts.length == 4 && isDatePart(parts[0], 4) && isDatePart(parts[1], 2) && isDatePart(parts[2], 2)) {
            return "/" + parts[0] + "/" + parts[1] + "/" + parts[2] + "/" + parts[3] + ".html";
        }

        // Fall back to a flat path for non-dated keys (for example scaffold posts like hello-world).
        return "/" + key + ".html";
    }

    private static boolean isDatePart(String part, int expectedLength) {
        if (part == null || part.length() != expectedLength) {
            return false;
        }
        for (int index = 0; index < part.length(); index++) {
            if (!Character.isDigit(part.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    public static String formatXmlSchema(LocalDate date) {
        if(date == null) return "";
        return date.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String getPathForPage(int pageForPath) {
        if (pageForPath == 1) {
            return "/index.html";
        } else {
            return "/page/" + pageForPath + "/index.html";
        }
    }
}

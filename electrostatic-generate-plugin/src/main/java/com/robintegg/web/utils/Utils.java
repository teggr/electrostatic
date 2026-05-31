package com.robintegg.web.utils;

import com.robintegg.web.site.Site;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Utils {
    public static String relativeUrl(String url) {
        return url;
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
        int index = key.indexOf("-");
        String year = key.substring(0,index);
        key = key.substring(index+ 1);

        index = key.indexOf("-");
        String month = key.substring(0,index);
        key = key.substring(index+ 1);

        index = key.indexOf("-");
        String day = key.substring(0,index);
        String title = key.substring(index+ 1);

        // = key.indexOf(".");
        //String title = key.substring(0,index);
        //key = key.substring(index+ 1);

        return "/" + year + "/" + month + "/" + day + "/" + title + ".html";
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

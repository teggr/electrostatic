package electrostatic.utils;

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

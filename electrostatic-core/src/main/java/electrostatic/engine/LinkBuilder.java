package electrostatic.engine;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.nio.file.Path;

@Slf4j
public class LinkBuilder {

  public static String link(String from, String to) {

    from = from.startsWith("/") ? from : "/" + from;
    to = to.startsWith("/") ? to : "/" + to;

    log.info("from {} to {}", from, to);

    Path fromPath = Path.of(from);
    Path toPath = Path.of(to);

    String string = fromPath.relativize(toPath).toString();
    if(string.equals("..")) {
      string = ".";
    }
    string = string.replaceAll("\\\\", "/");
    string = string.replaceFirst("\\.\\./", "");
    return string;

  }

}

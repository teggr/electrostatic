package electrostatic.engine;

import j2html.tags.DomContent;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Builder
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class Page {

    @ToString.Include
    private final String path;
    @ToString.Include
    private boolean includeMenu;
    @ToString.Include
    private final Map<String, List<String>> data;
    private final Function<RenderModel, DomContent> renderFunction;
    @Builder.Default
    private final Pageable pageable = new Pageable(1,-1);

    public String getTitle() {
        return Optional
                .ofNullable(data.get("title")).map(l -> l.get(0))
                .orElse(null);
    }

    public String getUrl() {
        return path;
    }

    public String getListTitle() {
        List<String> stringList = data.get("list_title");
        if (stringList == null) {
            return null;
        }
        return stringList.get(0);
    }

    public LocalDate getModifiedDate() {
        List<String> dates = data.get("modified_date");
        if (dates == null) {
            return null;
        }
        return LocalDate.parse(dates.get(0));
    }

    public LocalDate getDate() {
        List<String> dates = data.get("date");
        if (dates == null) {
            return null;
        }
        return LocalDate.parse(dates.get(0));
    }

    public List<String> getAuthor() {
        return data.getOrDefault("authors", Collections.emptyList());
    }

    public String getSubtitle() {
        List<String> stringList = data.get("subtitle");
        if (stringList == null) {
            return null;
        }
        return stringList.get(0);
    }

    public String getTag() {
        List<String> stringList = data.get("tag");
        if (stringList == null) {
            return "";
        }
        return stringList.get(0);
    }

    public String getExcerpt() {
        return null;
    }

    public String getImageUrl() {
        List<String> stringList = data.get("image");
        if (stringList == null) {
            return null;
        }
        return stringList.get(0);
    }

  public String getPodnewsUrl() {
      List<String> stringList = data.get("podnews_url");
      if (stringList == null) {
          return null;
      }
      return stringList.get(0);
  }

    public String getCategory() {
        List<String> stringList = data.get("category");
        if (stringList == null) {
            return "";
        }
        return stringList.get(0);
    }

    public List<String> get(String key) {
        return data.getOrDefault(key, List.of());
    }

}

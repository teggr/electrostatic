package com.robintegg.web.engine;

import j2html.tags.DomContent;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public String getDescription() {
        List<String> stringList = data.get("description");
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        return stringList.get(0);
    }

    public String getImageUrl() {
        List<String> stringList = data.get("image");
        if (stringList == null) {
            return null;
        }
        return stringList.get(0);
    }

    public String getImageWidth() {
        List<String> stringList = data.get("image_width");
        if (stringList == null) {
            return null;
        }
        return stringList.get(0);
    }

    public String getImageHeight() {
        List<String> stringList = data.get("image_height");
        if (stringList == null) {
            return null;
        }
        return stringList.get(0);
    }

    public String getImageAlt() {
        List<String> stringList = data.get("image_alt");
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

  public String getWebsite() {
      List<String> stringList = data.get("website");
      if (stringList == null) {
          return null;
      }
      return stringList.get(0);
  }

  public String getIcon() {
      List<String> stringList = data.get("icon");
      if (stringList == null) {
          return null;
      }
      return stringList.get(0);
  }

  public String getItunesUrl() {
      List<String> stringList = data.get("itunes_url");
      if (stringList == null) {
          return null;
      }
      return stringList.get(0);
  }

  public String getRssUrl() {
      List<String> stringList = data.get("rss_url");
      if (stringList == null) {
          return null;
      }
      return stringList.get(0);
  }

  public String getPocketcastsUrl() {
      List<String> stringList = data.get("pocketcasts_url");
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

    public String getLayout() {
        List<String> stringList = data.get("layout");
        if (stringList == null || stringList.isEmpty()) {
            return "default";
        }
        return stringList.get(0);
    }

    public boolean isPost() {
        return "post".equals(getLayout());
    }

}

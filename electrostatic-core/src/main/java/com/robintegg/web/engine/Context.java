package com.robintegg.web.engine;

import com.robintegg.web.site.Site;
import lombok.Data;

@Data
public class Context {

  private Site site = new Site();
  private String environment = "local";

  public String getLang() {
    // TODO: lang="{{ page.lang | default: site.lang | default: "en" }}"
    return "en";
  }

}

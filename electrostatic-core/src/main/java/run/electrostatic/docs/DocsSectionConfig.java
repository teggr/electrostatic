package run.electrostatic.docs;

import run.electrostatic.site.Site;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class DocsSectionConfig {

  private static final String DEFAULT_SECTIONS = "installation,guides,plugins";
  private static final String DEFAULT_LABELS = "installation=Installation,guides=Guides,plugins=Plugins";

  private DocsSectionConfig() {
  }

  public static List<DocsSection> defaults() {
    return fromRaw(DEFAULT_SECTIONS, DEFAULT_LABELS);
  }

  public static List<DocsSection> fromSite(Site site) {
    if (site == null) {
      return defaults();
    }
    return fromRaw(site.getDocsSections(), site.getDocsSectionLabels());
  }

  private static List<DocsSection> fromRaw(String sectionsRaw, String labelsRaw) {
    String normalizedSections = normalizeSections(sectionsRaw);
    String normalizedLabels = normalizeLabels(labelsRaw);

    List<String> keys = parseSectionKeys(normalizedSections);
    Map<String, String> labels = parseLabels(normalizedLabels);

    List<DocsSection> sections = new ArrayList<>();
    for (String key : keys) {
      String label = labels.getOrDefault(key, toLabel(key));
      sections.add(new DocsSection(key, label));
    }
    return sections;
  }

  private static String normalizeSections(String sectionsRaw) {
    if (sectionsRaw == null || sectionsRaw.isBlank()) {
      return DEFAULT_SECTIONS;
    }
    return sectionsRaw;
  }

  private static String normalizeLabels(String labelsRaw) {
    if (labelsRaw == null || labelsRaw.isBlank()) {
      return DEFAULT_LABELS;
    }
    return labelsRaw;
  }

  private static List<String> parseSectionKeys(String sectionsRaw) {
    List<String> keys = new ArrayList<>();
    for (String token : sectionsRaw.split(",")) {
      String normalized = normalizeKey(token);
      if (!normalized.isBlank() && !keys.contains(normalized)) {
        keys.add(normalized);
      }
    }
    return keys;
  }

  private static Map<String, String> parseLabels(String labelsRaw) {
    Map<String, String> labels = new LinkedHashMap<>();
    for (String token : labelsRaw.split(",")) {
      String part = token.trim();
      int separator = part.indexOf('=');
      if (separator <= 0 || separator == part.length() - 1) {
        continue;
      }

      String key = normalizeKey(part.substring(0, separator));
      String label = part.substring(separator + 1).trim();
      if (!key.isBlank() && !label.isBlank()) {
        labels.put(key, label);
      }
    }
    return labels;
  }

  private static String normalizeKey(String token) {
    return token == null ? "" : token.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
  }

  private static String toLabel(String key) {
    StringBuilder label = new StringBuilder();
    boolean capitalize = true;
    for (char ch : key.toCharArray()) {
      if (ch == '-') {
        label.append(' ');
        capitalize = true;
      } else if (capitalize) {
        label.append(Character.toUpperCase(ch));
        capitalize = false;
      } else {
        label.append(ch);
      }
    }
    return label.toString();
  }

}

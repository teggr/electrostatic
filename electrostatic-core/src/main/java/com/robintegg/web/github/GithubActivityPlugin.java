package com.robintegg.web.github;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.site.Site;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class GithubActivityPlugin implements ContentTypePlugin {

  public static final GithubActivityPlugin INSTANCE = new GithubActivityPlugin();

  public static GithubActivityPlugin create() {
    return INSTANCE;
  }

  private final List<GitHubRepository> activeRepositories = new ArrayList<>();

  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {

    // Fetch GitHub activity for the site
    if (site.getGithubUsername() != null && !site.getGithubUsername().isEmpty()) {
      activeRepositories.addAll(fetchGitHubActivity(site));
    }

  }

  public List<GitHubRepository> getActiveRepositories() {
    return activeRepositories;
  }

  private static List<GitHubRepository> fetchGitHubActivity(Site site) {
    try {

      GitHubClient gitHubClient = new GitHubClient();

      // Exclude personal website and specific repositories
      Set<String> excludedRepos = Set.of("robintegg", "yorkshire-golf");

      List<GitHubRepository> activeRepos = gitHubClient.getActiveRepositories(
        site.getGithubUsername(),
        excludedRepos,
        3  // Max 3 repositories
      );

      log.info("Loaded {} active GitHub repositories", activeRepos.size());
      return activeRepos;

    } catch (Exception e) {

      log.error("Failed to fetch GitHub activity, continuing without it", e);
      // Don't fail the build if GitHub API is unavailable
      return List.of();

    }
  }

  public void registerPlugins() {
    Plugins.contentTypePlugins.add(this);
  }

}

package com.robintegg.web.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Client for fetching GitHub repository information
 */
@Slf4j
public class GitHubClient {
    
    private static final String GITHUB_API_BASE = "https://api.github.com";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public GitHubClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Fetch the most active repositories for a GitHub user, excluding specified repositories.
     * 
     * @param username GitHub username
     * @param excludeRepos Set of repository names to exclude
     * @param maxResults Maximum number of repositories to return
     * @return List of active GitHub repositories
     */
    public List<GitHubRepository> getActiveRepositories(String username, Set<String> excludeRepos, int maxResults) {
        try {
            log.info("Fetching repositories for user: {}", username);
            
            String url = GITHUB_API_BASE + "/users/" + username + "/repos?type=owner&sort=pushed&per_page=100";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "robintegg-website-generator")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("Failed to fetch repositories. Status: {}, Body: {}", response.statusCode(), response.body());
                return List.of();
            }
            
            return parseRepositories(response.body(), excludeRepos, maxResults);
            
        } catch (IOException | InterruptedException e) {
            log.error("Error fetching GitHub repositories", e);
            return List.of();
        }
    }
    
    private List<GitHubRepository> parseRepositories(String json, Set<String> excludeRepos, int maxResults) throws IOException {
        JsonNode repos = objectMapper.readTree(json);
        List<GitHubRepository> result = new ArrayList<>();
        
        for (JsonNode repo : repos) {
            String name = repo.get("name").asText();
            
            // Skip excluded repositories
            if (excludeRepos.contains(name)) {
                log.debug("Excluding repository: {}", name);
                continue;
            }
            
            // Skip forks
            if (repo.get("fork").asBoolean()) {
                log.debug("Skipping fork: {}", name);
                continue;
            }
            
            GitHubRepository repository = GitHubRepository.builder()
                    .name(name)
                    .description(repo.has("description") && !repo.get("description").isNull() 
                            ? repo.get("description").asText() 
                            : "")
                    .htmlUrl(repo.get("html_url").asText())
                    .stargazersCount(repo.get("stargazers_count").asInt())
                    .pushedAt(repo.get("pushed_at").asText())
                    .build();
            
            result.add(repository);
            
            if (result.size() >= maxResults) {
                break;
            }
        }
        
        log.info("Found {} active repositories", result.size());
        return result;
    }
}

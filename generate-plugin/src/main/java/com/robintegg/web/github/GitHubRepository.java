package com.robintegg.web.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a GitHub repository with basic information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepository {
    
    private String name;
    private String description;
    private String htmlUrl;
    private int stargazersCount;
    private String pushedAt;
    
}

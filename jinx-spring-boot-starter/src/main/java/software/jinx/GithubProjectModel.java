package software.jinx;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GithubProjectModel {
	

	private final String username;
	private final String project;
	
	public String getLink() {
		return "https://github.com/" + username + "/" + project;
	}
	
}

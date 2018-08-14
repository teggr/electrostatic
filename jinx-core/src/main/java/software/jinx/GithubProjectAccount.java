package software.jinx;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GithubProjectAccount implements Account<GithubProjectAccount> {
	

	private final String username;
	private final String project;
	
	@Override
	public String getName() {
		return "github";
	}
	
	@Override
	public GithubProjectAccount get() {
		return this;
	}
	
	public String getLink() {
		return "https://github.com/" + username + "/" + project;
	}
	
}

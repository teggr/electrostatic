package site.electrostatic;

import groovy.transform.builder.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class GithubProjectAccount implements Account<GithubProjectAccount> {

	private String username;
	private String project;
	
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

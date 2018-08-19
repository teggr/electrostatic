package site.electrostatic.social;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.electrostatic.Account;

@Getter
@Setter
@RequiredArgsConstructor
public class FlickrAccount implements Account<FlickrAccount> {

	private String username;
	private String project;
	
	@Override
	public String getName() {
		return "github";
	}
	
	@Override
	public FlickrAccount get() {
		return this;
	}
	
	public String getLink() {
		return "https://github.com/" + username + "/" + project;
	}
	
}

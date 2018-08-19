package site.electrostatic.social;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.electrostatic.Account;

@Getter
@Setter
@RequiredArgsConstructor
public class TwitterAccount implements Account<TwitterAccount> {

	private String username;
	
	@Override
	public String getName() {
		return "twitter";
	}
	
	@Override
	public TwitterAccount get() {
		return this;
	}
	
	public String getHandle() {
		return String.format("@%s", username);
	}
	
	public String getLink() {
		return "https://twitter.com/" + username;
	}

}

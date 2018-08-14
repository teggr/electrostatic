package software.jinx;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TwitterAccount implements Account<TwitterAccount> {

	private final String username;
	
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

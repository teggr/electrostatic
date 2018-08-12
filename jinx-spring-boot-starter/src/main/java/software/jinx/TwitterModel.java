package software.jinx;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TwitterModel {

	private final String username;
	
	public String getHandle() {
		return String.format("@%s", username);
	}
	
	public String getLink() {
		return "https://twitter.com/" + username;
	}

}

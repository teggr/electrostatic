package software.jinx;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import lombok.Builder;

@Builder
public class Accounts {

	private TwitterAccount twitter;
	private GithubProjectAccount githubProject;

	public Collection<Account<?>> getActive() {
		return Collections.unmodifiableCollection(
				Arrays.asList(twitter, githubProject).stream().filter(a -> a != null).collect(Collectors.toList()));
	}

}

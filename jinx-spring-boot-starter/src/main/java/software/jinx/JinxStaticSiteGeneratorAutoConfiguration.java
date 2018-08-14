package software.jinx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("software.jinx")
public class JinxStaticSiteGeneratorAutoConfiguration {

	@Bean
	@ConfigurationProperties("jinx.twitter")
	public TwitterAccount twitterAccount() {
		return new TwitterAccount();
	}
	
	@Bean
	@ConfigurationProperties("jinx.github")
	public GithubProjectAccount  githubProjectAccount() {
		return new GithubProjectAccount();
	}
	
}

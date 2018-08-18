package site.electrostatic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("site.electrostatic")
public class ElectroStaticSiteGeneratorAutoConfiguration {

	@Bean
	@ConfigurationProperties("electro.twitter")
	public TwitterAccount twitterAccount() {
		return new TwitterAccount();
	}
	
	@Bean
	@ConfigurationProperties("electro.github")
	public GithubProjectAccount  githubProjectAccount() {
		return new GithubProjectAccount();
	}
	
}

package software.jinx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;

@Configuration
public class SiteGeneratorAutoConfiguration {

	@Bean
	public SiteGeneratorRunner siteGeneratorCommandLineLauncher(SiteGeneratorProperties properties, TemplateEngine  templateEngine) {
		return new SiteGeneratorRunner(properties, templateEngine);
	}

	@Bean
	public SiteGeneratorProperties siteGeneratorProperties() {
		return new SiteGeneratorProperties();
	}

}

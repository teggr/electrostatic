package software.jinx;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix="site.gen")
public class SiteGeneratorProperties {

	private String siteDirectoryLocation = ".";	
	private String outputDirectoryLocation = "/site";
	
}

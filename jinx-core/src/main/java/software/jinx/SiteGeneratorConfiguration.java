package software.jinx;

import java.io.File;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class SiteGeneratorConfiguration {

	private final File baseDirectory;	
	private final File outputDirectory;
	
}

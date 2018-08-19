package site.electrostatic;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class ThemeConfiguration {

	private boolean includePosts = true;
	private boolean includePages = true;
	
}

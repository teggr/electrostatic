package site.electrostatic;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ToString
public class SiteConfiguration {

	private String lang = "en";
	private String title = "";
	private String author = "";
	private String email = "";
	private String description = "";
	
	private boolean showExcerpts = true;
	
	private String dateFormat = "dd/MM/yyyy";
	
}

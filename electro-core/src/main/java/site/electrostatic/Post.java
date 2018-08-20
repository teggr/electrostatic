package site.electrostatic;

import java.time.LocalDate;

public interface Post {

	LocalDate getDate();
	
	String getUrl();
	
	String getTitle();
	
	String getExcerpt();
	
}

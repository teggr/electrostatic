package electrostatic.site;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="site")
@XmlAccessorType(XmlAccessType.FIELD)
public class Site {

    private String title;
    private Author author;
    private String description;
    private String email;
    private String twitterUsername;
    private String githubUsername;
    private String linkedinUsername;
    private String url;
    private String baseUrl;

    public boolean showExcerpts() {
        return false;
    }

    public String resolveUrl(String resolveUrl) {
        if(!resolveUrl.startsWith("/")) {
            resolveUrl = "/" + resolveUrl;
        }
        return url + baseUrl + resolveUrl;
    }
}

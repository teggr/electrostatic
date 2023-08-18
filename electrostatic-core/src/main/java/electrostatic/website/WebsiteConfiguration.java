package electrostatic.website;


import electrostatic.plugins.ThemePlugin;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WebsiteConfiguration {

    private ThemePlugin themePlugin;

}

package electrostatic.web;

import electrostatic.theme.product.ProductThemePlugin;
import electrostatic.website.WebSiteConfigurer;
import electrostatic.website.WebsiteConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElectroStaticSite implements WebSiteConfigurer {

    @Override
    public WebsiteConfiguration configure(WebsiteConfiguration.WebsiteConfigurationBuilder configuration) {

        log.info("configuring electrostatic site");

        return configuration
                .themePlugin(
                        ProductThemePlugin
                                .create()
                                .landingPage(LandingPage.create())
                )
                .build();

    }
}

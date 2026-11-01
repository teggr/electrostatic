package io.github.teggr.electrostatic.site;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class SitePlugin {

    private static JAXBContext jaxbContext = null;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Site.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Site loadFromFile(Path path) {

        Path siteConfigFile = path.resolve("site-config.xml");
        log.info("site config: " + siteConfigFile.toAbsolutePath());

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Site site = (Site) unmarshaller.unmarshal(Files.newInputStream(siteConfigFile));
        configurePostHog(site);

        return site;
    }

    private static void configurePostHog(Site site) {
        String projectKey = System.getenv("POSTHOG_KEY");
        String apiHost = System.getenv("POSTHOG_HOST");

        PostHog posthog = site.getPosthog();
        if (projectKey != null && !projectKey.isBlank()) {
            if (posthog == null) {
                posthog = new PostHog();
            }
            posthog.setProjectKey(projectKey.trim());
        }

        if (apiHost != null && !apiHost.isBlank()) {
            if (posthog == null) {
                posthog = new PostHog();
            }
            posthog.setApiHost(apiHost.trim());
        }

        if (posthog != null) {
            if (posthog.getApiHost() == null || posthog.getApiHost().isBlank()) {
                posthog.setApiHost("https://eu.i.posthog.com");
            }
            site.setPosthog(posthog);
        }
    }
}

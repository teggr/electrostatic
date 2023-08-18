package electrostatic.site;

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

        if (Files.exists(siteConfigFile)) {

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            return (Site) unmarshaller.unmarshal(Files.newInputStream(siteConfigFile));

        } else {

            Site site = new Site();
            site.setTitle("electrostatic Site");
            site.setAuthor(new Author("Auto", "new@electrostatic.website"));
            site.setDescription("A new electrostatic generated site");
            site.setEmail("new@electrostatic.website");
            site.setTwitterUsername("electrostatic");
            site.setGithubUsername("electrostatic");
            site.setLinkedinUsername("electrostatic");
            site.setUrl("");
            site.setBaseUrl("");

            return site;

        }

    }

}

package com.robintegg.web.site;

import com.robintegg.web.github.GitHubClient;
import com.robintegg.web.github.GitHubRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

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

        return site;

    }

}

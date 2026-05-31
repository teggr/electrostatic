package run.electrostatic.docs;

import org.junit.jupiter.api.Test;
import run.electrostatic.site.Site;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocsSectionConfigTest {

    @Test
    void fromSite_shouldUseDefaultsWhenConfigIsMissing() {
        Site site = new Site();

        List<DocsSection> sections = DocsSectionConfig.fromSite(site);

        assertEquals(List.of("installation", "guides", "plugins"), sections.stream().map(DocsSection::key).toList());
        assertEquals(List.of("Installation", "Guides", "Plugins"), sections.stream().map(DocsSection::label).toList());
    }

    @Test
    void fromSite_shouldSupportProjectSpecificSectionSets() {
        Site site = new Site();
        site.setDocsSections("installation,guides,commands");
        site.setDocsSectionLabels("installation=Install,guides=How To,commands=Commands");

        List<DocsSection> sections = DocsSectionConfig.fromSite(site);

        assertEquals(List.of("installation", "guides", "commands"), sections.stream().map(DocsSection::key).toList());
        assertEquals(List.of("Install", "How To", "Commands"), sections.stream().map(DocsSection::label).toList());
    }
}

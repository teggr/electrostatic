package electrostatic.feed;

import electrostatic.content.IndexContent;
import electrostatic.engine.RenderModel;
import electrostatic.feed.atom.*;
import electrostatic.feed.atom.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Feed {

  private static JAXBContext jaxbContext = null;

  static {
    try {
      jaxbContext = JAXBContext.newInstance(AtomFeed.class);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  private List<FeedEntry> feedEntries = new ArrayList<>();

  public String getPath() {
    return "feed.xml";
  }

  public String getContent(RenderModel renderModel) {

    feedEntries.sort(Comparator.comparing(FeedEntry::getDate).reversed());

    AtomFeed feed = AtomFeed.builder()
        .generator(Generator.builder()
            .uri("https://github.com/tipsy/j2html")
            .version("v1.6.0")
            .value("j2html")
            .build())
        .link(List.of(
            Link.builder()
                .href(renderModel.getContext().getSite().resolveUrl(getPath()))
                .rel("self")
                .type("application/atom+xml")
                .build(),
            Link.builder()
                .href(renderModel.getContext().getSite().getUrl())
                .rel("alternative")
                .type("text/html")
                .build()
        ))
        .updated(OffsetDateTime.now())
        .id(renderModel.getContext().getSite().resolveUrl(getPath()))
        .title(Title.builder()
            .type("html")
            .value(renderModel.getContext().getSite().getTitle())
            .build())
        .subtitle(renderModel.getContext().getSite().getDescription())
        .author(List.of(
            Author.builder()
                .name(renderModel.getContext().getSite().getAuthor().getName())
                .build()
        ))
        .entry(feedEntries.stream()
            .map(fe -> mapToAtomEntry(renderModel, fe))
            .toList()
        )
        .build();

    try {

      // https://stackoverflow.com/questions/6895486/jaxb-need-namespace-prefix-to-all-the-elements
      // https://eclipse-ee4j.github.io/jaxb-ri/3.0.0/docs/ch05.html
      // https://ibytecode.com/blog/jaxb-marshalling-and-unmarshalling-cdata-block/
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty("org.glassfish.jaxb.characterEscapeHandler", new MyCharacterEscapeHandler());
      marshaller.setProperty("org.glassfish.jaxb.xmlDeclaration", true);
      StringWriter writer = new StringWriter();
      marshaller.marshal(feed, writer);

      return writer.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private Entry mapToAtomEntry(RenderModel renderModel, FeedEntry entry) {
    return
        Entry.builder()
            .title(Title.builder()
                .type("html")
                .value(entry.getTitle())
                .build())
            .link(List.of(
                    Link.builder()
                        .href(entry.getUrl())
                        .rel("alternate")
                        .type("text/html")
                        .title(entry.getTitle())
                        .build()
                )
            )
            .published(entry.getDate().atStartOfDay().atOffset(ZoneOffset.UTC))
            .updated(entry.getModifiedDate().atStartOfDay().atOffset(ZoneOffset.UTC))
            .id(feedId(entry.getUrl()))
            .content(Content.builder()
                .type("html")
                .xmlBase(entry.getUrl())
                .value(entry.getContent().apply(renderModel).render())
                .build())
            .author(List.of(
                Author.builder()
                    .name(entry.getAuthor())
                    .build()
            ))
            .category(entry.getTags().stream()
                .map(tag -> Category.builder()
                    .term(tag)
                    .build()
                )
                .toList()
            )
            .summary(Summary.builder()
                .type("html")
                .value(entry.getExcerpt().apply(renderModel).render())
                .build())
            .mediaThumbnail(MediaThumbnail.builder()
                .url(entry.getImageUrl())
                .build())
            .mediaContent(MediaContent.builder()
                .medium("image")
                .url(entry.getImageUrl())
                .build())
            .build();
  }

  private String feedId(String url) {
    return url.replaceAll("\\.html", "");
  }

  public void addEntry(FeedEntry entry) {
    this.feedEntries.add(entry);
  }

  public void addContent(IndexContent post) {
    addEntry(FeedEntry.builder()
        .title(post.getTitle())
        .url(post.getUrl())
        .date(post.getDate())
        .modifiedDate(post.getDate())
        .content(post::getContent)
        .author(post.getAuthor())
        .tags(post.getTags())
        .excerpt(post::getExcerpt)
        .imageUrl(post.getImage())
        .build());
  }

}

package com.robintegg.web.theme.includes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import static j2html.TagCreator.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SEO {

  public static final String PROPERTY = "property";
  public static final String DESCRIPTION = "description";
  private static final ObjectMapper objectMapper;

  static {

    objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.registerModule(new JavaTimeModule());

  }

  public static DomContent render(RenderModel renderModel) {
    
    boolean isPost = renderModel.getPage().isPost();
    
    // Resolve absolute image URL if present
    String absoluteImageUrl = Utils.resolveImageUrl(
        renderModel.getPage().getImageUrl(), 
        renderModel.getContext().getSite()
    );
    
    return each(
        iffElse(
            renderModel.getPage().getTitle() != null,
            title(renderModel.getPage().getTitle() + " | " + renderModel.getContext().getSite().getTitle()),
            title(renderModel.getContext().getSite().getTitle() + " | " + renderModel.getContext().getSite().getDescription())
        ),
        meta().withName("generator").withContent("j2html"),
        iffElse(
            renderModel.getPage().getTitle() != null,
            meta().attr(PROPERTY, "og:title").withContent(renderModel.getPage().getTitle()),
            meta().attr(PROPERTY, "og:title").withContent(renderModel.getContext().getSite().getTitle())
        ),
        meta().withName("author").withContent(renderModel.getContext().getSite().getAuthor().getName()),
        meta().attr(PROPERTY, "og:locale").withContent(renderModel.getContext().getLang()),
        iffElse(
            renderModel.getPage().getDescription() != null,
            meta().withName(DESCRIPTION).withContent(renderModel.getPage().getDescription()),
            iffElse(
                renderModel.getPage().getExcerpt() != null,
                meta().withName(DESCRIPTION).withContent(renderModel.getPage().getExcerpt()),
                meta().withName(DESCRIPTION).withContent(renderModel.getContext().getSite().getDescription())
            )
        ),
        iffElse(
            renderModel.getPage().getDescription() != null,
            meta().attr(PROPERTY, "og:description").withContent(renderModel.getPage().getDescription()),
            iffElse(
                renderModel.getPage().getExcerpt() != null,
                meta().attr(PROPERTY, "og:description").withContent(renderModel.getPage().getExcerpt()),
                meta().attr(PROPERTY, "og:description").withContent(renderModel.getContext().getSite().getDescription())
            )
        ),
        link().withRel("canonical").withHref(renderModel.getContext().getSite().resolveUrl(renderModel.getPage().getUrl())),
        meta().attr(PROPERTY, "og:url").withContent(renderModel.getContext().getSite().resolveUrl(renderModel.getPage().getUrl())),
        meta().attr(PROPERTY, "og:site_name").withContent(renderModel.getContext().getSite().getTitle()),
        iff(
            absoluteImageUrl != null,
            meta().attr(PROPERTY, "og:image").withContent(absoluteImageUrl)
        ),
        iff(
            absoluteImageUrl != null,
            meta().withName("twitter:image").withContent(absoluteImageUrl)
        ),
        iff(
            renderModel.getPage().getImageWidth() != null,
            meta().attr(PROPERTY, "og:image:width").withContent(renderModel.getPage().getImageWidth())
        ),
        iff(
            renderModel.getPage().getImageHeight() != null,
            meta().attr(PROPERTY, "og:image:height").withContent(renderModel.getPage().getImageHeight())
        ),
        iff(
            renderModel.getPage().getImageAlt() != null,
            meta().attr(PROPERTY, "og:image:alt").withContent(renderModel.getPage().getImageAlt())
        ),
        meta().attr(PROPERTY, "og:type").withContent(isPost ? "article" : "website"),
        renderArticleTags(renderModel, isPost),
        meta().withName("twitter:card").withContent("summary_large_image"),
        iff(
            renderModel.getPage().getImageAlt() != null,
            meta().withName("twitter:image:alt").withContent(renderModel.getPage().getImageAlt())
        ),
        iffElse(
            renderModel.getPage().getTitle() != null,
            meta().attr(PROPERTY, "twitter:title").withContent(renderModel.getPage().getTitle()),
            meta().attr(PROPERTY, "twitter:title").withContent(renderModel.getContext().getSite().getAuthor().getName())
        ),
        meta().withName("twitter:site").withContent("@" + renderModel.getContext().getSite().getTwitterUsername()),
        meta().withName("twitter:creator").withContent("@" + renderModel.getContext().getSite().getTwitterUsername()),
        script()
            .withType("application/ld+json")
            .with(
                new UnescapedText(ldJson(renderModel, absoluteImageUrl))
            )
    );
  }
  
  private static DomContent renderArticleTags(RenderModel renderModel, boolean isPost) {
    if (!isPost) {
      return text("");
    }
    
    return each(
        iff(renderModel.getPage().getDate() != null,
            meta().attr(PROPERTY, "article:published_time").withContent(
                renderModel.getPage().getDate().atStartOfDay().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME)
            )
        ),
        iff(!renderModel.getPage().getAuthor().isEmpty(),
            each(renderModel.getPage().getAuthor(), author ->
                meta().attr(PROPERTY, "article:author").withContent(author)
            )
        ),
        iff(renderModel.getPage().getCategory() != null && !renderModel.getPage().getCategory().isEmpty(),
            meta().attr(PROPERTY, "article:section").withContent(renderModel.getPage().getCategory())
        ),
        iff(renderModel.getPage().getData().containsKey("tags"),
            each(renderModel.getPage().getData().get("tags"), tag ->
                meta().attr(PROPERTY, "article:tag").withContent(tag)
            )
        )
    );
  }

  @SneakyThrows
  private static String ldJson(RenderModel renderModel, String absoluteImageUrl) {

    ObjectNode author = objectMapper.createObjectNode();
    author.put("@type", "Person");
    author.put("name", renderModel.getContext().getSite().getAuthor().getName());

    ObjectNode jsonObject = objectMapper.createObjectNode();
    jsonObject.put("@context", "https://schema.org");

    // Check if this is a blog post
    boolean isBlogPost = renderModel.getPage().isPost();

    if (isBlogPost) {
      // Use BlogPosting type for blog posts
      jsonObject.put("@type", "BlogPosting");
      jsonObject.put("headline", renderModel.getPage().getTitle());
      jsonObject.set("author", author);
      
      // Add publication date
      if (renderModel.getPage().getDate() != null) {
        jsonObject.put("datePublished", renderModel.getPage().getDate().toString());
      }
      
      // Add modified date if available
      if (renderModel.getPage().getModifiedDate() != null) {
        jsonObject.put("dateModified", renderModel.getPage().getModifiedDate().toString());
      }
      
      // Add image if available
      if (absoluteImageUrl != null) {
        jsonObject.put("image", absoluteImageUrl);
      }
      
      // Add URL
      jsonObject.put("url", renderModel.getContext().getSite().resolveUrl(renderModel.getPage().getUrl()));
      
      // Add description/excerpt if available
      if (renderModel.getPage().getDescription() != null) {
        jsonObject.put(DESCRIPTION, renderModel.getPage().getDescription());
      } else if (renderModel.getPage().getExcerpt() != null) {
        jsonObject.put(DESCRIPTION, renderModel.getPage().getExcerpt());
      } else {
        jsonObject.put(DESCRIPTION, renderModel.getContext().getSite().getDescription());
      }
    } else {
      // Use WebSite type for non-blog pages
      jsonObject.put("@type", "WebSite");
      jsonObject.set("author", author);
      
      // Add description for WebSite type
      if (renderModel.getPage().getDescription() != null) {
        jsonObject.put(DESCRIPTION, renderModel.getPage().getDescription());
      } else {
        jsonObject.put(DESCRIPTION, renderModel.getContext().getSite().getDescription());
      }
      jsonObject.put("headline", renderModel.getContext().getSite().getTitle());
      jsonObject.put("name", renderModel.getContext().getSite().getTitle());
      jsonObject.put("url", renderModel.getContext().getSite().getBaseUrl());
    }

    return objectMapper.writeValueAsString(jsonObject);

  }
}

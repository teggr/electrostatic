package electrostatic.content.post;

import electrostatic.content.IndexContent;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;

import java.time.LocalDate;
import java.util.List;

class PostIndexedContent {
  public static IndexContent map(Post post) {
    return new IndexContent() {

      @Override
      public LocalDate getDate() {
        return post.getDate();
      }

      @Override
      public String getUrl() {
        return post.getUrl();
      }

      @Override
      public String getTitle() {
        return post.getTitle();
      }

      @Override
      public DomContent getExcerpt(RenderModel renderModel) {
        return post.getExcerpt(renderModel);
      }

      @Override
      public String getCategory() {
        return post.getCategory();
      }

      @Override
      public DomContent getContent(RenderModel renderModel) {
        return post.getContent(renderModel);
      }

      @Override
      public List<String> getTags() {
        return post.getTags();
      }

      @Override
      public String getAuthor() {
        return post.getAuthor();
      }

      @Override
      public String getImage() {
        return post.getImage();
      }
    };
  }
}

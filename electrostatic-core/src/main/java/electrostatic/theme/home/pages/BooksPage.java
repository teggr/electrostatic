package electrostatic.theme.home.pages;

import electrostatic.content.book.Book;
import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class BooksPage {

  public static Page create() {

    return Page.builder()
        .path("/books/index.html")
        .includeMenu(true)
        .data(Map.of(
            "layout", List.of("default"),
            "title", List.of("Books"),
            "permalink", List.of("/books"),
            "list_title", List.of("Books")
        ))
        .renderFunction(BooksPage::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {
    return div()
        .withClass("home")
        .with(
            ul()
                .with(
                    iff(
                        renderModel.getContentModel().getContentOfType(Book.class).size() > 0,
                        each(
                            h2()
                                .withClass("post-list-heading")
                                .withText(renderModel.getPage().getListTitle()),
                            ul()
                                .withClass("post-list")
                                .with(
                                    each(renderModel.getContentModel().getContentOfType(Book.class), book -> {
                                      return li()
                                          .with(
                                              h3()
                                                  .with(
                                                      a()
                                                          .withClass("post-link")
                                                          .withHref(Utils.relativeUrl(book.getUrl()))
                                                          .withText(Utils.relativeUrl(book.getTitle()))
                                                  ),
                                              p(book.getSubtitle()),
                                              img()
                                                  .withWidth("150px")
                                                  .withSrc(book.getImageUrl()),
                                              iff(
                                                  book.getTags().size() > 0,
                                                  ul()
                                                      .withClass("post-tags")
                                                      .with(
                                                          each(book.getTags(), tag -> {
                                                            return li()
                                                                .with(
                                                                    a()
                                                                        .withHref(Utils.relativeUrl("/tags/" + tag))
                                                                        .withText(tag)
                                                                );
                                                          })
                                                      )
                                              )
                                          );
                                    })
                                )
                        )

                    )
                )
        );
  }

}

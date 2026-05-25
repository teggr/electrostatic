package com.robintegg.web.theme.pages;

import com.robintegg.web.content.book.Book;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.Comparator;
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
            "title", List.of("Bookshelf"),
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
            h2()
                .withClass("post-list-heading")
                .withText(renderModel.getPage().getListTitle()),
            div()
                .withClass("book-search")
                .withStyle("margin-bottom: 20px;")
                .with(
                    input()
                        .withType("text")
                        .withId("book-search-input")
                        .withPlaceholder("Search books...")
                        .withStyle("width: 100%; padding: 10px; font-size: 16px; border: 1px solid #ccc; border-radius: 4px;")
                ),
            iff(
                renderModel.getContentModel().getContentOfType(Book.class).size() > 0,
                div()
                    .withId("books-container")
                    .withClass("books-grid")
                    .withStyle("display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;")
                    .with(
                        each(renderModel.getContentModel().getContentOfType(Book.class).stream()
                            .sorted(Comparator.comparing(Book::getAddedDate).reversed())
                            .toList(), book -> {
                          return div()
                              .withClass("book-card")
                              .attr("data-title", book.getTitle().toLowerCase())
                              .attr("data-subtitle", book.getSubtitle().toLowerCase())
                              .attr("data-author", book.getAuthor().toLowerCase())
                              .attr("data-tags", String.join(" ", book.getTags()).toLowerCase())
                              .withStyle("border: 1px solid #ddd; padding: 15px; border-radius: 8px; background: white;")
                              .with(
                                  iff(
                                      book.getImageUrl() != null,
                                      div()
                                          .withStyle("width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 4px; margin-bottom: 10px; overflow: hidden;")
                                          .with(
                                              img()
                                                  .withSrc(book.getImageUrl())
                                                  .withAlt(book.getTitle())
                                                  .withStyle("max-width: 100%; max-height: 100%; width: auto; height: auto; object-fit: contain;")
                                          )
                                  ),
                                  h3()
                                      .withStyle("margin-top: 0; margin-bottom: 10px;")
                                      .with(
                                          a()
                                              .withClass("post-link")
                                              .withHref(Utils.relativeUrl(book.getUrl()))
                                              .withText(book.getTitle())
                                      ),
                                  p()
                                      .withStyle("font-size: 14px; color: #666; line-height: 1.5; margin-bottom: 5px;")
                                      .withText(book.getSubtitle()),
                                  p()
                                      .withStyle("font-size: 13px; color: #888; margin-bottom: 10px;")
                                      .withText("by " + book.getAuthor()),
                                  iff(
                                      book.getTags().size() > 0,
                                      ul()
                                          .withClass("post-tags")
                                          .withStyle("margin-top: 10px;")
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
            ),
            script()
                .withType("text/javascript")
                .with(rawHtml("""
                    document.addEventListener('DOMContentLoaded', function() {
                      const searchInput = document.getElementById('book-search-input');
                      const bookCards = document.querySelectorAll('.book-card');
                      
                      searchInput.addEventListener('input', function() {
                        const searchTerm = this.value.toLowerCase();
                        
                        bookCards.forEach(function(card) {
                          const title = card.getAttribute('data-title');
                          const subtitle = card.getAttribute('data-subtitle');
                          const author = card.getAttribute('data-author');
                          const tags = card.getAttribute('data-tags');
                          
                          if (title.includes(searchTerm) || 
                              subtitle.includes(searchTerm) || 
                              author.includes(searchTerm) ||
                              tags.includes(searchTerm)) {
                            card.style.display = 'block';
                          } else {
                            card.style.display = 'none';
                          }
                        });
                      });
                    });
                    """))
        );
  }

}

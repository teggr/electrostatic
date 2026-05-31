package com.robintegg.web.content.book;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookEntry {

  @XmlElement(name="details")
  private BookDetails bookDetails;

  private Bookshelf bookshelf;

  public BookDetails getBookDetails() {
    return bookDetails;
  }

  public void setBookDetails(BookDetails bookDetails) {
    this.bookDetails = bookDetails;
  }

  public Bookshelf getBookshelf() {
    return bookshelf;
  }

  public void setBookshelf(Bookshelf bookshelf) {
    this.bookshelf = bookshelf;
  }

}

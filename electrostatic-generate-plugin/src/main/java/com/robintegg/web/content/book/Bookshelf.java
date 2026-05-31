package com.robintegg.web.content.book;

import com.robintegg.web.utils.LocalDateXmlAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
public class Bookshelf {

  @XmlElement
  @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
  private LocalDate added;

  public LocalDate getAdded() {
    return added;
  }

  public void setAdded(LocalDate added) {
    this.added = added;
  }
}

package com.robintegg.web.utils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

  public LocalDate unmarshal(String v) throws Exception {
    return LocalDate.parse(v);
  }

  public String marshal(LocalDate v) throws Exception {
    return v.format(DateTimeFormatter.ISO_DATE_TIME);
  }

}
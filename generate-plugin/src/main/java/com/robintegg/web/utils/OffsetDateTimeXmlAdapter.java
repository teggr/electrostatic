package com.robintegg.web.utils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeXmlAdapter extends XmlAdapter<String, OffsetDateTime> {

  public OffsetDateTime unmarshal(String v) throws Exception {
    return OffsetDateTime.parse(v);
  }

  public String marshal(OffsetDateTime v) throws Exception {
    return v.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

}
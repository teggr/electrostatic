package com.robintegg.web.feed;

import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class MyCharacterEscapeHandler implements CharacterEscapeHandler {
  @Override
  public void escape(char[] chars, int i, int i1, boolean b, Writer writer) throws IOException {
    writer.write(chars, i, i1);
  }
}

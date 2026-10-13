package io.github.teggr.electrostatic.engine;

import io.github.teggr.electrostatic.content.staticfiles.StaticFile;

public interface ContentModelVisitor {

    void page(Page page);

    void file(StaticFile file);

}

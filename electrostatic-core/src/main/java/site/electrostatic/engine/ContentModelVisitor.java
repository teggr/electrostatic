package site.electrostatic.engine;

import site.electrostatic.content.staticfiles.StaticFile;

public interface ContentModelVisitor {

    void page(Page page);

    void file(StaticFile file);

}

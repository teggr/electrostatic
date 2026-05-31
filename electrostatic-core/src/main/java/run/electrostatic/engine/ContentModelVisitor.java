package run.electrostatic.engine;

import run.electrostatic.content.staticfiles.StaticFile;

public interface ContentModelVisitor {

    void page(Page page);

    void file(StaticFile file);

}

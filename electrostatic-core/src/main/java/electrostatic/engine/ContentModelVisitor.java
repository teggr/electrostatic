package electrostatic.engine;

import electrostatic.content.staticfiles.StaticFile;

public interface ContentModelVisitor {

    void page(Page page);

    void file(StaticFile file);

}

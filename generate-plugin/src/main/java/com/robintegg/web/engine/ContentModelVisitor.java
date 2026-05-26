package com.robintegg.web.engine;

import com.robintegg.web.content.staticfiles.StaticFile;

public interface ContentModelVisitor {

    void page(Page page);

    void file(StaticFile file);

}

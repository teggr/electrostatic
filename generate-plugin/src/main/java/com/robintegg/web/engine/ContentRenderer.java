package com.robintegg.web.engine;

import com.robintegg.web.content.staticfiles.StaticFile;
import j2html.TagCreator;
import j2html.rendering.FlatHtml;
import j2html.tags.DomContent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

@Slf4j
public class ContentRenderer {

  public void render(Path outputDirectory, Map<String, Layout> layouts, ContentModel contentModel, Context context) {

    log.info("render=start");

    // TODO: assess
    // rendering of pre calculated resources? just resource name + function to final render?
    // where does the website come into it? writer (filesystem writer)?; external to the rendering (this knows
    // about locations and files)
    RenderModel renderModel = new RenderModel();
    renderModel.setContext(context);
    renderModel.setContentModel(contentModel);

    contentModel.visit(new ContentModelVisitor() {

      // TODO: rendering of a page needs to be less attached to the content model

      @Override
      public void file(StaticFile file) {
        log.info("file={}", file);

        byte [] fileContent = file.getRenderFunction().apply(renderModel);

        String path = file.getPath();
        if (path.startsWith("/")) {
          path = path.substring(1);
        }
        var outputFile = outputDirectory.resolve(path);

        // write to file
        try {
          Files.createDirectories(outputFile.getParent().toAbsolutePath());
          Files.write(outputFile.toAbsolutePath(), fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

      }

      @Override
      public void page(Page page) {
        log.info("page={}", page);

        renderModel.setPage(page);

        Map<String, List<String>> data = page.getData();
        String layoutName = data.getOrDefault("layout", List.of("default")).get(0);

        log.info("layout={}", layoutName);

        DomContent domContent = page.getRenderFunction().apply(renderModel);

        // do i need to add the layout? yes
        Layout layout = layouts.get(layoutName);

        // need to update the content for this model here
        renderModel.setContent(domContent);
        DomContent layoutContent = layout.getRenderFunction().apply(renderModel);

        Map<String, List<String>> layoutData = layout.getData();
        List<String> outLayoutName = layoutData != null ? layoutData.get("layout") : null;
        if (outLayoutName != null) {
          layoutName = outLayoutName.get(0);
          log.info("outerlayout={}", layoutName);
          layout = layouts.get(layoutName);
          renderModel.setContent(layoutContent);
          layoutContent = layout.getRenderFunction().apply(renderModel);
        }

        // this is what we want to render
        StringBuilder render = null;
        try {
          render = layoutContent.render(FlatHtml.inMemory());
          //System.out.println(render);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        String path = page.getPath();
        if (path.startsWith("/")) {
          path = path.substring(1);
        }
        var outputFile = outputDirectory.resolve(path);

        // write to file
        try {
          Files.createDirectories(outputFile.getParent().toAbsolutePath());
          Files.writeString(outputFile.toAbsolutePath(), TagCreator.document() + render.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        renderModel.reset();

      }

    });

  }



}

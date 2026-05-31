package dev.rebelcraft.electrostatic.cli;

import com.robintegg.web.engine.WebSiteBuilder;
import com.robintegg.web.theme.DefaultThemePlugin;
import fi.iki.elonen.NanoHTTPD;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(
    name = "serve",
    mixinStandardHelpOptions = true,
    description = "Build the static site and serve it locally"
)
public class ServeCommand implements Callable<Integer> {

    @Option(names = {"-p", "--port"}, defaultValue = "8080", description = "Port to serve on (default: ${DEFAULT-VALUE})")
    private int port;

    @Option(names = {"--base-url"}, description = "Override the base URL for the site")
    private String baseUrl;

    @Override
    public Integer call() throws Exception {
        new WebSiteBuilder(DefaultThemePlugin.create()).build(baseUrl);

        var workingDirectory = Paths.get(System.getProperty("workingDirectory", ""));
        var siteDirectory = workingDirectory.resolve("target/site").toAbsolutePath();

        NanoHTTPD server = new NanoHTTPD(port) {
            @Override
            public Response serve(IHTTPSession session) {
                String uri = session.getUri();
                if (uri.endsWith("/")) {
                    uri += "index.html";
                }
                File file;
                try {
                    file = siteDirectory.resolve(uri.substring(1)).toRealPath().toFile();
                } catch (Exception e) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
                }
                if (!file.toPath().startsWith(siteDirectory) || !file.isFile()) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
                }
                try {
                    String mimeType = NanoHTTPD.getMimeTypeForFile(uri);
                    return newChunkedResponse(Response.Status.OK, mimeType, new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Not Found: " + uri);
                }
            }
        };

        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Serving site from " + siteDirectory);
        System.out.println("Listening on http://localhost:" + port);
        System.out.println("Press Ctrl+C to stop.");

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        Thread.currentThread().join();

        return 0;
    }

}

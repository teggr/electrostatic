package electrostatic.build;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BuildContext {

    private final String workingDirectory;
    private final String basePackage;
    private final String environment;
    private final boolean drafts;
    private final String themeName;

}

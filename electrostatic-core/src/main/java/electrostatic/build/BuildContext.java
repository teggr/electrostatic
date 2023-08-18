package electrostatic.build;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BuildContext {

    private String workingDirectory;
    private String environment;
    private boolean drafts;

}

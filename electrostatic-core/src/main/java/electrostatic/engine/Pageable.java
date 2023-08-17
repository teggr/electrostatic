package electrostatic.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Pageable {

    private final int page;
    private final int pageSize;

}

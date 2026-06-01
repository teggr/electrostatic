package run.electrostatic.core;

public final class GenerationOptionsContext {

    private static final ThreadLocal<GenerationOptions> CURRENT = new ThreadLocal<>();

    private GenerationOptionsContext() {
    }

    public static GenerationOptions current() {
        GenerationOptions options = CURRENT.get();
        return options != null ? options : GenerationOptions.fromBaseUrl(null);
    }

    public static void set(GenerationOptions options) {
        CURRENT.set(options);
    }

    public static void clear() {
        CURRENT.remove();
    }
}
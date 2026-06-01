package site.electrostatic.core;

public record GenerationOptions(String baseUrl, boolean includeDrafts) {

    public static GenerationOptions defaults() {
        return new GenerationOptions(null, false);
    }

    public static GenerationOptions fromBaseUrl(String baseUrl) {
        return new GenerationOptions(normalizeBaseUrl(baseUrl), false);
    }

    public GenerationOptions withBaseUrl(String baseUrl) {
        return new GenerationOptions(normalizeBaseUrl(baseUrl), includeDrafts);
    }

    public GenerationOptions withIncludeDrafts(boolean includeDrafts) {
        return new GenerationOptions(baseUrl, includeDrafts);
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return null;
        }
        return baseUrl;
    }
}
package run.electrostatic.docs;

public record DocsSection(String key, String label) {

  public String folderName() {
    return "_" + key;
  }

  public String indexPath() {
    return "/" + key + "/index.html";
  }

  public String basePath() {
    return "/" + key + "/";
  }

}

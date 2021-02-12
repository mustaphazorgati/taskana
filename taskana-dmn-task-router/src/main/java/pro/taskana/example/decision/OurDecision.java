package pro.taskana.example.decision;

import java.util.List;

public class OurDecision {
  private final List<OurRule> path;
  private final int clazz;

  public OurDecision(List<OurRule> path, int clazz) {
    this.path = path;
    this.clazz = clazz;
  }

  public List<OurRule> getPath() {
    return path;
  }

  public int getClazz() {
    return clazz;
  }

  @Override
  public String toString() {
    return "Decision{" + "path=" + path + ", clazz=" + clazz + '}';
  }
}

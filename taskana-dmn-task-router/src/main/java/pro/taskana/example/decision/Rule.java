package pro.taskana.example.decision;

public class Rule {
  private final int attribIndex;
  private final double splitPoint;
  private final int index;

  /**
   * Create a single Rule instace.
   *
   * @param attribIndex which attribute was used for splitting
   * @param splitPoint at which value is the dataset split (only for numeric rules)
   * @param index index of the child. For numeric attributes: 0 --> attrib <= split, 1 --> attrib >
   *     split. For nominal attributes: index of the value
   *     (Instances.attribute(attribIndex).value(index))
   */
  public Rule(int attribIndex, double splitPoint, int index) {
    this.attribIndex = attribIndex;
    this.splitPoint = splitPoint;
    this.index = index;
  }

  public int getAttribIndex() {
    return attribIndex;
  }

  public double getSplitPoint() {
    return splitPoint;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public String toString() {
    return "Rule{"
        + "attribIndex="
        + attribIndex
        + ", splitPoint="
        + splitPoint
        + ", index="
        + index
        + '}';
  }
}

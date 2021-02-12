package pro.taskana.example.decision;

public class Rule {
    private final int attribIndex;
    private final double splitPoint;
    private final boolean left;


    public Rule(int attribIndex, double splitPoint, boolean left) {
        this.attribIndex = attribIndex;
        this.splitPoint = splitPoint;
        this.left = left;
    }

    public int getAttribIndex() {
        return attribIndex;
    }

    public double getSplitPoint() {
        return splitPoint;
    }

    public boolean isLeft() {
        return left;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "attribIndex=" + attribIndex +
                ", splitPoint=" + splitPoint +
                ", left=" + left +
                '}';
    }
}

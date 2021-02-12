package pro.taskana.example.decision;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.C45Split;
import weka.classifiers.trees.j48.ClassifierTree;

import java.util.ArrayList;
import java.util.List;

public class OurTree extends J48 {

    public List<Decision> linearizeTree () throws Exception {
        List<Decision> result = new ArrayList<>();
        List<Rule> path = new ArrayList<>();
        visitNode(m_root, path, result);
        return result;
    }

    private void visitNode(ClassifierTree node, List<Rule> path, List<Decision> decisions) {
        if (node.isLeaf()) {
            Decision decision = new Decision(path, node.getLocalModel().distribution().maxClass());
            decisions.add(decision);
            System.out.println(String.format("I'm at an leaf: %s", decision));
        } else {
            ArrayList<Rule> leftPath = new ArrayList<>(path);
            leftPath.add(buildRuleFromNode(node, true));
            visitNode(
                    node.getSons()[0],
                    leftPath,
                    decisions
                    );

            ArrayList<Rule> rightPath = new ArrayList<>(path);
            rightPath.add(buildRuleFromNode(node, false));
            visitNode(
                    node.getSons()[1],
                    rightPath,
                    decisions
            );
        }
    }

    private Rule buildRuleFromNode(ClassifierTree node, boolean left) {
        if (node.getLocalModel() instanceof C45Split) {
            return new Rule(
                    ((C45Split) node.getLocalModel()).attIndex(),
                    ((C45Split) node.getLocalModel()).splitPoint(),
                    left
            );
        } else {
            System.out.println(node.getLocalModel().getClass());
            return null;
        }
    }

}

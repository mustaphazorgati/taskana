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
            for (int i = 0; i < node.getSons().length; i++) {
                ArrayList<Rule> decentPath = new ArrayList<>(path);
                decentPath.add(buildRuleFromNode(node, i));
                visitNode(
                        node.getSons()[i],
                        decentPath,
                        decisions
                );
            }
        }
    }

    private Rule buildRuleFromNode(ClassifierTree node, int index) {
        if (node.getLocalModel() instanceof C45Split) {
            return new Rule(
                    ((C45Split) node.getLocalModel()).attIndex(),
                    ((C45Split) node.getLocalModel()).splitPoint(),
                    index
            );
        } else {
            System.out.println(node.getLocalModel().getClass());
            return null;
        }
    }

}

package pro.taskana.example.decision;

import java.util.ArrayList;
import java.util.List;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.C45Split;
import weka.classifiers.trees.j48.ClassifierTree;

public class OurTree extends J48 {

  public List<OurDecision> linearizeTree() {
    List<OurDecision> result = new ArrayList<>();
    List<OurRule> path = new ArrayList<>();
    visitNode(m_root, path, result);
    return result;
  }

  private void visitNode(ClassifierTree node, List<OurRule> path, List<OurDecision> decisions) {
    if (node.isLeaf()) {
      OurDecision decision = new OurDecision(path, node.getLocalModel().distribution().maxClass());
      decisions.add(decision);
    } else {
      for (int i = 0; i < node.getSons().length; i++) {
        ArrayList<OurRule> decentPath = new ArrayList<>(path);
        decentPath.add(buildRuleFromNode(node, i));
        visitNode(node.getSons()[i], decentPath, decisions);
      }
    }
  }

  private OurRule buildRuleFromNode(ClassifierTree node, int index) {
    if (node.getLocalModel() instanceof C45Split) {
      return new OurRule(
          ((C45Split) node.getLocalModel()).attIndex(),
          ((C45Split) node.getLocalModel()).splitPoint(),
          index);
    } else {
      System.out.println(node.getLocalModel().getClass());
      return null;
    }
  }
}

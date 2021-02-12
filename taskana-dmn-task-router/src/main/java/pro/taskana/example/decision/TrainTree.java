package pro.taskana.example.decision;

import java.io.File;
import java.util.List;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.Text;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveByName;

public class TrainTree {

  public static final String TRAINING_DATA_SET_FILENAME = "titanic.csv";

  public static Instances getDataSet(String fileName) throws Exception {
    int classIdx = 0;
    CSVLoader loader = new CSVLoader();
    loader.setSource(TrainTree.class.getResourceAsStream(fileName));
    Instances dataSet = loader.getDataSet();
    dataSet.setClassIndex(classIdx);

    RemoveByName removeName = new RemoveByName();
    removeName.setOptions(new String[] {"-E", "^Name$"});
    removeName.setInputFormat(dataSet);
    dataSet = Filter.useFilter(dataSet, removeName);

    NumericToNominal num2nom = new NumericToNominal();
    num2nom.setOptions(new String[] {"-R", "1"});
    num2nom.setInputFormat(dataSet);
    dataSet = Filter.useFilter(dataSet, num2nom);

    return dataSet;
  }

  public static void main(String[] args) throws Exception {
    Instances dataSet = TrainTree.getDataSet(TRAINING_DATA_SET_FILENAME);
    OurTree classifier = buildModel(dataSet);
    System.out.println(classifier);
    List<OurDecision> decisions = classifier.linearizeTree();

    printDecisions(dataSet, decisions);

    DmnModelInstance modelInstance = Dmn.createEmptyModel();

    Definitions definitions = modelInstance.newInstance(Definitions.class);
    definitions.setNamespace("http://camunda.org/schema/1.0/dmn");
    definitions.setName("definitions");
    definitions.setId("definitions");
    modelInstance.setDefinitions(definitions);

    Decision decision = modelInstance.newInstance(Decision.class);
    decision.setId("workbasketRouting");
    decision.setName("Workbasket Routing");
    definitions.addChildElement(decision);

    DecisionTable decisionTable = modelInstance.newInstance(DecisionTable.class);
    decisionTable.setId("DecisionTable_1pdawfb");
    decision.addChildElement(decisionTable);

    InputExpression inputExpression = modelInstance.newInstance(InputExpression.class);
    inputExpression.setId("InputExpression_1");
    inputExpression.setTypeRef("string");
    Text text = modelInstance.newInstance(Text.class);
    text.setTextContent("classificationName");
    inputExpression.setText(text);

    Input input = modelInstance.newInstance(Input.class);
    input.setId("Input_1");
    input.setLabel("Classification name");
    input.addChildElement(inputExpression);
    decisionTable.addChildElement(input);

    Output output = modelInstance.newInstance(Output.class);
    output.setId("Output_1");
    output.setLabel("Workbasket Key");
    output.setName("workbasketKey");
    output.setTypeRef("string");
    decisionTable.addChildElement(output);

    Dmn.validateModel(modelInstance);

    File file = new File("src/main/resources/pro/taskana/example/routing/test.dmn");
    file.createNewFile();
    Dmn.writeModelToFile(file, modelInstance);

  }

  private static void printDecisions(Instances dataSet, List<OurDecision> decisions) {
    for (OurDecision d : decisions) {
      for (OurRule r : d.getPath()) {
        Attribute a = dataSet.attribute(r.getAttribIndex());
        if (a.isNominal()) {
          System.out.println(a.name() + " == " + a.value(r.getIndex()));
        } else {
          if (r.getIndex() == 0) {
            System.out.println(a.name() + " <= " + r.getSplitPoint());
          } else {
            System.out.println(a.name() + " > " + r.getSplitPoint());
          }
        }
      }
      System.out.println("Decision: " + d.getClazz());
      System.out.println("---------------\n");
    }
  }

  public static OurTree buildModel(Instances trainingDataSet) throws Exception {

    OurTree classifier = new OurTree();

    classifier.buildClassifier(trainingDataSet);

    return classifier;
  }
}

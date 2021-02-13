package pro.taskana.example.decision;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveByName;

public class TrainTree {

  private static final String TRAINING_DATA_SET_FILENAME = "titanic.csv";
  private static final String OUTPUT_FILE_NAME = "dmn-table.dmn";

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

    DmnModelInstance model = convertToDmnModel(dataSet, decisions);
    File file = new File("src/main/resources/pro/taskana/example/routing/" + OUTPUT_FILE_NAME);
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      Dmn.writeModelToStream(outputStream, model);
    }
  }

  public static OurTree buildModel(Instances trainingDataSet) throws Exception {

    OurTree classifier = new OurTree();

    classifier.buildClassifier(trainingDataSet);

    return classifier;
  }

  private static DmnModelInstance convertToDmnModel(
      Instances dataSet, List<OurDecision> decisions) {
    DmnBuilder builder =
        new DmnBuilder()
            .decisionTable("workbasketRouting", "Workbasket Routing")
            .input("classificationName", "number", "task.custom1", "Classification name")
            .output("string", "workbasketKey", "Workbasket key")
            .output("string", "domain", "Domain");
    for (OurDecision decision : decisions) {
      Map<Integer, List<OurRule>> groupedRules =
          decision.getPath().stream().collect(Collectors.groupingBy(OurRule::getAttribIndex));

      for (Map.Entry<Integer, List<OurRule>> entry : groupedRules.entrySet()) {}
    }

    builder
        .rule("1")
        .input("custom1", "number(input) <10")
        .output("workbasketKey", "\"GPK_KSC\"")
        .output("domain", "\"DOMAIN_A\"")
        .persist();

    DmnModelInstance model = builder.build();
    Dmn.validateModel(model);
    return model;
  }

  private static String mapToTask(Attribute desc) {
    switch (desc.name()) {
      case "Pclass":
        return "task.custom1";
      case "Name":
        return "task.custom2";
      case "Sex":
        return "task.custom3";
      case "Age":
        return "task.custom4";
      case "Siblings/Spouses Aboard":
        return "task.custom5";
      case "Parents/Children Aboard":
        return "task.custom6";
      case "Fare":
        return "task.custom7";
      default:
        throw new RuntimeException(
            String.format("Can't map Attribute '%s' to Task attribute.", desc.name()));
    }
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
}

package pro.taskana.example.decision;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveByName;

import pro.taskana.example.decision.DmnBuilder.RuleBuilder;

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
    System.out.println("Collecting dataset...");
    Instances dataSet = getDataSet(TRAINING_DATA_SET_FILENAME);
    System.out.println("Training Decision Tee...");
    OurTree classifier = buildModel(dataSet);
    System.out.println(classifier);
    System.out.println("Converting Tree to DMN Model...");
    List<OurDecision> decisions = classifier.linearizeTree();
    DmnModelInstance model = convertToDmnModel(dataSet, decisions);
    File file = new File("src/main/resources/pro/taskana/example/routing/" + OUTPUT_FILE_NAME);
    System.out.println("writing DMN Model to file: " + file.getAbsolutePath());
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      Dmn.writeModelToStream(outputStream, model);
    }
    System.out.println("DONE! HAPPY ROUTING!");
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
            .output("string", "workbasketKey", "Workbasket key")
            .output("string", "domain", "Domain");

    int attributeCount = dataSet.numAttributes();
    for (int i = 0; i < attributeCount; i++) {
      if (i != dataSet.classIndex()) {
        Attribute attribute = dataSet.attribute(i);
        String typeRef = attribute.isNominal() ? "string" : "double";
        String taskAttribute = mapToTaskAttribute(attribute);
        builder.input(String.valueOf(i), typeRef, taskAttribute, attribute.name());
      }
    }

    for (int i = 0; i < decisions.size(); i++) {
      OurDecision decision = decisions.get(i);
      Map<Integer, List<OurRule>> groupedRules =
          decision.getPath().stream().collect(Collectors.groupingBy(OurRule::getAttribIndex));

      RuleBuilder ruleBuilder = builder.rule(String.valueOf(i));
      for (int j = 0; j < attributeCount; j++) {
        if (j != dataSet.classIndex()) {
          List<OurRule> rules = groupedRules.get(j);
          if (rules == null) {
            ruleBuilder.input(String.valueOf(j), "");
          } else {
            String expression;
            if (dataSet.attribute(j).isNominal()) {
              expression =
                  rules.stream()
                      .map(rule -> dataSet.attribute(rule.getAttribIndex()).value(rule.getIndex()))
                      .map(expr -> "\"" + expr + "\"")
                      .collect(Collectors.joining(", "));
            } else {
              if (rules.size() == 1) {
                // TODO: filter out >0 and <= 0 ?
                OurRule rule = rules.get(0);
                expression = (rule.getIndex() == 0 ? "<=" : ">") + " " + rule.getSplitPoint();
              } else {
                Optional<OurRule> lowestUpperBound =
                    rules.stream()
                        .filter(rule -> rule.getIndex() == 0)
                        // TODO: filter out <= 0?
                        // .filter(rule -> rule.getSplitPoint() != 0)
                        .min(Comparator.comparing(OurRule::getSplitPoint));
                Optional<OurRule> highestLowerBound =
                    rules.stream()
                        .filter(rule -> rule.getIndex() == 1)
                        // TODO: filter out > 0?
                        // .filter(rule -> rule.getSplitPoint() != 0)
                        .max(Comparator.comparing(OurRule::getSplitPoint));
                if (lowestUpperBound.isPresent() && highestLowerBound.isPresent()) {
                  expression =
                      String.format(
                          "]%.2f..%.2f]",
                          highestLowerBound.get().getSplitPoint(),
                          lowestUpperBound.get().getSplitPoint());
                } else if (lowestUpperBound.isPresent()) {
                  expression = "<= " + lowestUpperBound.get().getSplitPoint();
                } else if (highestLowerBound.isPresent()) {
                  expression = "> " + highestLowerBound.get().getSplitPoint();
                } else {
                  expression = "";
                }
              }
            }
            ruleBuilder.input(String.valueOf(j), expression);
          }
        }
      }
      ruleBuilder
          .output("workbasketKey", decision.getClazz() == 0 ? "\"DIED\"" : "\"SURVIVED\"")
          .output("domain", "\"DOMAIN_A\"")
          .persist();
    }

    DmnModelInstance model = builder.build();
    Dmn.validateModel(model);
    return model;
  }

  private static String mapToTaskAttribute(Attribute desc) {
    switch (desc.name()) {
      case "Pclass":
        return "task.custom1";
      case "Sex":
        return "task.custom2";
      case "Age":
        return "task.custom3";
      case "Siblings/Spouses Aboard":
        return "task.custom4";
      case "Parents/Children Aboard":
        return "task.custom5";
      case "Fare":
        return "task.custom6";
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

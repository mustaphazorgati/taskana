package pro.taskana.example.decision;

import java.io.IOException;
import java.util.List;
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
    List<Decision> decisions = classifier.linearizeTree();

    for (Decision d : decisions) {

      for (Rule r : d.getPath()) {
        Attribute a = dataSet.attribute(r.getAttribIndex());
        if (a.isNominal()) {
          System.out.println(a.name() + " == " + a.value(r.getIndex()));
        } else {
          if (r.getIndex() == 0) System.out.println(a.name() + " <= " + r.getSplitPoint());
          else System.out.println(a.name() + " > " + r.getSplitPoint());
        }
      }
      System.out.println("Decision: " + d.getClazz());
      System.out.println("---------------\n");
    }

    //        DmnEngine dmnEngine =
    //            DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    //
    //
    //
    //        DmnModelInstance dmnModel = Dmn.createEmptyModel();

  }

  public static OurTree buildModel(Instances trainingDataSet) throws Exception {

    OurTree classifier = new OurTree();

    classifier.buildClassifier(trainingDataSet);

    return classifier;
  }
}

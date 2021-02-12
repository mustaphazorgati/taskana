package pro.taskana.example.decision;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveByName;

import java.io.IOException;

public class TrainTree {

    /** file names are defined*/
    public static final String TRAINING_DATA_SET_FILENAME="titanic.csv";

    /**
     * This method is to load the data set.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Instances getDataSet(String fileName) throws Exception {
        /**
         * we can set the file i.e., loader.setFile("finename") to load the data
         */
        int classIdx = 0;
        CSVLoader loader = new CSVLoader();
        loader.setSource(TrainTree.class.getResourceAsStream(fileName));
        Instances dataSet = loader.getDataSet();
        dataSet.setClassIndex(classIdx);

        RemoveByName removeName =  new RemoveByName();
        removeName.setOptions(new String[]{"-E", "^Name$"});
        removeName.setInputFormat(dataSet);
        dataSet = Filter.useFilter(dataSet, removeName);

        NumericToNominal num2nom =  new NumericToNominal();
        num2nom.setOptions(new String[]{"-R", "1"});
        num2nom.setInputFormat(dataSet);
        dataSet = Filter.useFilter(dataSet, num2nom);

        return dataSet;
    }

    public static void main(String[] args) throws Exception {
        Instances dataSet = TrainTree.getDataSet(TRAINING_DATA_SET_FILENAME);
        OurTree classifier = buildModel(dataSet);
        System.out.println(classifier);
        classifier.linearizeTree();

    }

    public static OurTree buildModel(Instances trainingDataSet) throws Exception {

        OurTree classifier = new OurTree();

        classifier.buildClassifier(trainingDataSet);

        return classifier;

    }

}
